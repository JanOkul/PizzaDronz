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
 *  Location class helps with A* algorithm as it removes the need for multiple hashmaps.
 */
class Location{
    private final LngLat position;
    private Location cameFrom;
    private double gScore;
    private double hScore;
    private final double angle;     // The angle from the previous position to this position.

    Location(LngLat position, Location cameFrom, double gScore, double hScore, double angle){
        this.position = position;
        this.cameFrom = cameFrom;
        this.gScore = gScore;
        this.hScore = hScore;
        this.angle = angle;
    }

    LngLat getPosition(){return this.position;}

    void setHScore(double hScore){this.hScore = hScore;}

    Location getCameFrom(){return this.cameFrom;}
    void setCameFrom(Location cameFrom){this.cameFrom = cameFrom;}

    double getGScore(){return this.gScore;}
    void setGScore(double gScore){this.gScore = gScore;}

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

    protected ArrayList<Double> createFlightPath(LngLat startPosition, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {

        PriorityQueue<Location> openSet = new PriorityQueue<>(Comparator.comparingDouble(Location::getFScore));
        HashSet<Location> closedSet = new HashSet<>();

        // Add the start position to the open set.
        openSet.add(new Location(startPosition, null,0, hScore(startPosition, endPosition), Double.NaN));

        while (!openSet.isEmpty()) {
            Location current = openSet.poll();
            System.out.println(lngLatHandler.isCloseTo(current.getPosition(), endPosition));
            checkIfLeftCentralRegion(current.getPosition(), centralRegion);

            // Goal check
            if (lngLatHandler.isCloseTo(current.getPosition(), endPosition)) {
                System.out.println("hi");
                return reconstructPath(current);
            }

            closedSet.add(current);
            // Iterate over neighbors
            for (Location neighbor : getNeighbours(current, endPosition, noFlyZones, centralRegion)) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeGScore = current.getGScore() + lngLatHandler.distanceTo(current.getPosition(), neighbor.getPosition());

                if (tentativeGScore < neighbor.getGScore()) {
                    neighbor.setCameFrom(current);
                    neighbor.setGScore(tentativeGScore);
                    neighbor.setHScore(hScore(neighbor.getPosition(), endPosition));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
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

        while (current.getCameFrom() != null) {
            double angle = current.getAngle();
            System.out.println(angle);
            // Skips over the start angle, as the start position cannot have a previous angle.
            if (Double.isNaN(angle)) {
                continue;
            } else {

                path.add(angle);
            }
            current = current.getCameFrom();
        }

        return path;
    }

    private Location[] getNeighbours(Location currentLocation, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        LngLat currentPosition = currentLocation.getPosition();

        Location[] neighbours = new Location[16];
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

        else return SystemConstants.DRONE_MOVE_DISTANCE;
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
