package uk.ac.ed.inf.Flight;

import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

/**
 * An extended version of LngLat that represents a move made by the drone.
 */
class Move {
    private final LngLat position;
    private final Move cameFrom;
    private final double hScore;
    private final double angle;     // The angle from the previous position to this position.


    /**
     * Constructs a new Move.
     *
     * @param position The position of this move.
     * @param cameFrom The previous move in the path.
     * @param hScore   The heuristic cost estimate from this node to the end node.
     * @param angle    The angle from the previous position to this position.
     */
    Move(LngLat position, Move cameFrom, double hScore, double angle) {
        this.position = position;
        this.cameFrom = cameFrom;
        this.hScore = hScore;
        this.angle = angle;
    }

    /**
     * Gets the position of where this move is.
     *
     * @return A LngLat of the move's position
     */
    LngLat getPosition() {
        return this.position;
    }

    /**
     * Gets the move of where this move came from.
     * Used to nest moves in moves to get the final path.
     *
     * @return A Move object where this move came from.
     */
    Move getCameFrom() {
        return this.cameFrom;
    }

    /**
     * Gets the F-Score to get to this position.
     *
     * @return A double F-Score.
     */
    double getScore() {
        return this.hScore;
    }

    /**
     * The angle to get from {@link #cameFrom} to {@link #position}
     *
     * @return A double which is an angle.
     */
    double getAngle() {
        return this.angle;
    }
}

/**
 * Creates a flight path from a start position to an end position, avoiding no-fly zones
 * and ensures that it doesn't leave the central region, if it has re-entered it.
 */
public class PathGenerator {
    private final double[] DIRECTIONS;
    private final LngLatHandler lngLatHandler;
    private boolean leftCentralRegion;

    protected PathGenerator() {
        DIRECTIONS = new double[]{0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5};
        lngLatHandler = new LngLatHandler();
        leftCentralRegion = false;
    }


    /**
     * Calculates a flight path from a start position to an end position using
     * the A* pathfinding algorithm. The path avoids no-fly zones and considers
     * the central region restrictions. The G score has also been dropped as
     * all moves have the same cost of 1.5e-4.
     *
     * @param startPosition The starting position of the drone.
     * @param endPosition   The target position to reach.
     * @param noFlyZones    An array of regions where flying is not allowed.
     * @param centralRegion The central region where the drone cannot leave once it as re-entered.
     * @return A list of angles representing the drone's path, or null if no valid path is found.
     */
    protected ArrayList<Double> createFlightAngles(LngLat startPosition, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        PriorityQueue<Move> openSet = new PriorityQueue<>(Comparator.comparingDouble(Move::getScore));
        Set<Move> closedSet = new HashSet<>();

        // This also validates start and end through the LngLatHandler method distanceTo.
        double startingHScore = hScore(startPosition, endPosition);
        int maxIterations = 150_000;
        int noIterations = 0;

        Move start = new Move(startPosition, null, startingHScore, 0);
        leftCentralRegion = false;

        openSet.add(start);
        /*
            While the open set is empty and the program has not iterated less than 150k iterations. The drone would've
            moved just about 50 degrees of distance, roughly the distance between appleton and the equator. Therefore,
            if the drone cannot find a path after 150k moves, it is likely that there is no path, so we quit the algorithm.
         */
        while (!openSet.isEmpty() && noIterations <= maxIterations) {
            Move current = openSet.poll();  // Gets the move that is "closest" to the end position.
            canDroneLeaveCentral(centralRegion, current.getPosition());

            // If current location is close to the end position, reconstruct and return the path.
            if (lngLatHandler.isCloseTo(current.getPosition(), endPosition)) {
                return reconstructPath(current);
            }

            // Add current location to closed set to avoid checking it.
            closedSet.add(current);

            ArrayList<Move> neighbours = getNeighbours(current, endPosition, noFlyZones, centralRegion);
            for (Move neighbour : neighbours) {
                // Skip if this neighbour has already been evaluated.
                if (closedSet.contains(neighbour)) {
                    continue;
                }

                // Add neighbour to open set for next loop.
                openSet.add(neighbour);
            }
            noIterations++;
        }

        // Return an empty path if no path is found.
        return new ArrayList<>();
    }


    /**
     * Calculates the heuristic score for a position based on its distance to the end position by using Euclidean Distance.
     *
     * @param position    The current position.
     * @param endPosition The target end position.
     * @return The heuristic score based on the distance.
     */
    private double hScore(LngLat position, LngLat endPosition) {
        return lngLatHandler.distanceTo(position, endPosition);
    }


    /**
     * Generates neighboring moves from the current position, considering the possible directions,
     * no-fly zones, and central region rules. Disregard neighbours that are not legal moves, defined
     * in {@link #legalMove}.
     *
     * @param position      The current move.
     * @param endPosition   The target end move.
     * @param noFlyZones    An array of no-fly zones to avoid.
     * @param centralRegion The central region.
     * @return A list of possible neighboring moves.
     */
    private ArrayList<Move> getNeighbours(Move position, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        ArrayList<Move> neighbours = new ArrayList<>();

        // Get a neighbour for all directions
        for (double direction : DIRECTIONS) {
            LngLat currentPosition = position.getPosition();
            LngLat potentialNextPosition = lngLatHandler.nextPosition(currentPosition, direction);

            // Don't add the neighbour if it is not a legal move.
            if (!legalMove(currentPosition, potentialNextPosition, noFlyZones, centralRegion)) {
                continue;
            }

            double hScore = hScore(potentialNextPosition, endPosition);

            neighbours.add(new Move(potentialNextPosition, position, hScore, direction));
        }
        return neighbours;
    }

    /**
     * Determines whether a move from the current position to a potential position is legal,
     * considering no-fly zones and central region rules.
     *
     * @param currentPosition   The current position of the drone.
     * @param potentialPosition The potential next position of the drone.
     * @param noFlyZones        An array of no-fly zones.
     * @param centralRegion     The central region.
     * @return true if the move is legal, false otherwise.
     */
    private boolean legalMove(LngLat currentPosition, LngLat potentialPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {

        // Check if the move may be in a no-fly zone.
        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(potentialPosition, noFlyZone)) {
                return false;
            }
        }
        /*
            Return false if the move is currently in central,
            will not be in central, and has already left the central zone,
            true otherwise.
         */
        boolean currentlyInCentral = lngLatHandler.isInRegion(currentPosition, centralRegion);
        boolean potentiallyInCentral = lngLatHandler.isInRegion(potentialPosition, centralRegion);
        return !currentlyInCentral || potentiallyInCentral || !leftCentralRegion;
    }

    /**
     * Creates the angle path by recursively going up the Move came from variable until it hits
     * the null at the starting move.
     *
     * @param current The end position move from which to start reconstructing the path.
     * @return A list of angles representing the reconstructed path.
     */
    private ArrayList<Double> reconstructPath(Move current) {
        ArrayList<Double> path = new ArrayList<>();

        // Add the angle of the current Move until it reaches the starting move which was defined as null.
        while (current.getCameFrom() != null) {
            path.add(current.getAngle());
            current = current.getCameFrom();
        }
        // Reverse the path as the loop starts from the end position.
        Collections.reverse(path);
        return path;
    }

    /**
     * Updates the leftCentralRegion variable if the drone leaves the central region.
     * Once it leaves it never updates it again.
     *
     * @param centralRegion The central region.
     * @param dronePosition The current position of the drone.
     */
    private void canDroneLeaveCentral(NamedRegion centralRegion, LngLat dronePosition) {
        // No need to check if the drone has left central.
        if (!leftCentralRegion && !lngLatHandler.isInRegion(dronePosition, centralRegion)) {
            leftCentralRegion = true;
        }
    }
}
