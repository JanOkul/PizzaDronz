package uk.ac.ed.inf.Flight;

import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 *  An extension of the LngLat class, with additional information to be used in the A* algorithm.
 *  Replaces the need for HashMaps within the path generator
 */
class Location{
    private final LngLat position;
    private final Location cameFrom;
    private final double gScore;
    private final double hScore;
    private final double angle;     // The angle from the previous position to this position.

    Location(LngLat position, Location cameFrom, double gScore, double hScore, double angle){
        this.position = position;
        this.cameFrom = cameFrom;
        this.gScore = gScore;
        this.hScore = hScore;
        this.angle = angle;
    }
    // Getters
    LngLat getPosition(){return this.position;}
    Location getCameFrom(){return this.cameFrom;}
    double getFScore(){return this.gScore + this.hScore;}
    double getAngle(){return this.angle;}
}

/**
 *  Using the A* algorithm, the PathGenerator calculates the angles of the moves the drone needs to take to go
 *  from the start position to the end position.
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
     * Using the A* algorithm, finds the angles the drone needs to move in.
     * @param startPosition The starting position of the drone.
     * @param endPosition Destination of the drone.
     * @param noFlyZones Areas the drone cannot enter.
     * @param centralRegion The central region.
     * @return A list of angles that will be converted into a flight path later on.
     */
    protected ArrayList<Double> createFlightAngles(LngLat startPosition, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        // Comparator will set the priority of the locations based on their fScore.
        PriorityQueue<Location> openSet = new PriorityQueue<>(Comparator.comparingDouble(Location::getFScore));
        HashSet<Location> closedSet = new HashSet<>();

        // Add the start position to the open set.
        Location startingLocation = new Location(startPosition, null, 0.0, hScore(startPosition, endPosition), Double.NaN);
        openSet.add(startingLocation);

        while (!openSet.isEmpty()) {
            Location currentLocation = openSet.poll();
            checkIfLeftCentralRegion(currentLocation.getPosition(), centralRegion);

            // If the current location is the end position, return the path.
            if (lngLatHandler.isCloseTo(currentLocation.getPosition(), endPosition)) {
                return reconstructPath(currentLocation);
            }

            // Add the current location to the closed set.
            closedSet.add(currentLocation);

            // Get the neighbours of the current location.
            Location[] neighbours = getNeighbours(currentLocation, endPosition, noFlyZones, centralRegion);

            for (Location neighbour : neighbours) {
                // If the neighbour is in the closed set, skip it.
                if (closedSet.contains(neighbour)) {
                    continue;
                }
                // If the neighbour is not in the open set, add it.
                if (!openSet.contains(neighbour)) {
                    openSet.add(neighbour);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves the angle of a Location, until the parent is null, meaning it is the start position.
     * @param current The final position, when the drone has reached the end position.
     * @return An arraylist of the angles the drone needs to take to get to the end position.
     */
    private ArrayList<Double> reconstructPath(Location current) {
        ArrayList<Double> path = new ArrayList<>();

        // The starting node is always null, so stop when reached the start node.
        while (current.getCameFrom() != null) {
            double angle = current.getAngle();
            path.add(angle);
            current = current.getCameFrom();
        }
        return path;
    }

    /**
     * Calculates the 16 neighbours of a location.
     * @param currentLocation The current location of the drone.
     * @param endPosition  The end position of the drone.
     * @param noFlyZones The areas the drone cannot enter.
     * @param centralRegion The central region.
     * @return An array of the 16 neighbours.
     */
    private Location[] getNeighbours(Location currentLocation, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        LngLat currentPosition = currentLocation.getPosition();
        Location[] neighbours = new Location[16];

        // For each compass direction.
        for (int i = 0; i < 16; i++) {
            double angle = DIRECTIONS[i];
            LngLat nextPosition = lngLatHandler.nextPosition(currentPosition, angle);

            double gScore = gScore(currentPosition, nextPosition, noFlyZones, centralRegion);
            double hScore = hScore(nextPosition, endPosition);

            neighbours[i] = new Location(nextPosition, currentLocation, gScore, hScore, angle);
        }
        return neighbours;
    }

    /**
     * Calculates the hScore of a position.
     * @param position The position the drone is currently at.
     * @param endPosition The position the drone is trying to get to.
     * @return The euclidean distance between the two positions.
     */
    private double hScore(LngLat position, LngLat endPosition){
        return lngLatHandler.distanceTo(position, endPosition);
    }

    /**
     * Calculates the gScore of a position.
     * @param position The position the drone is currently at.
     * @param nextPosition The position the drone could be at.
     * @param noFlyZones The no-fly zones the drone cannot enter.
     * @param centralRegion The central region.
     * @return The cost of moving to a position.
     */
    private double gScore(LngLat position, LngLat nextPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        // Stops the drone from entering no-fly zones.
        for (NamedRegion region : noFlyZones) {
            if (lngLatHandler.isInRegion(position, region)) {
                return Double.POSITIVE_INFINITY;
            }
        }
        // Stops the drone from leaving the central region, if the drone has left and returned to the central region.
        if (lngLatHandler.isInRegion(position, centralRegion) && !lngLatHandler.isInRegion(nextPosition, centralRegion) && leftCentralRegion) {
            return Double.POSITIVE_INFINITY;
        }
        else {
            return SystemConstants.DRONE_MOVE_DISTANCE;    // LngLatHandler.distanceTo would be equivalent, so this can be called for efficiency.
        }
    }

    /**
     * Checks if the drone has left the central region.
     * @param position The position the drone is currently at.
     * @param centralRegion The central region.
     */
    private void checkIfLeftCentralRegion(LngLat position, NamedRegion centralRegion){
        if (!lngLatHandler.isInRegion(position, centralRegion)){
            leftCentralRegion = true;
        }
    }
}
