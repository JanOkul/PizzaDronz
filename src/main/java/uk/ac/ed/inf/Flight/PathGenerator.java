package uk.ac.ed.inf.Flight;

import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;


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


public class PathGenerator {
    private final double[] DIRECTIONS;
    private final LngLatHandler lngLatHandler;
    private boolean leftCentralRegion;

    protected PathGenerator() {
        DIRECTIONS = new double[]{0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5};
        lngLatHandler = new LngLatHandler();
        leftCentralRegion = false;
    }


    protected ArrayList<Double> createFlightAngles(LngLat startPosition, LngLat endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        PriorityQueue<Location> openSet = new PriorityQueue<>(Comparator.comparingDouble(Location::getFScore));
        Set<LngLat> closedSet = new HashSet<>();
        Location start = new Location(startPosition, null, 0, hScore(startPosition, endPosition), 0);

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Location current = openSet.poll();

            canDroneLeaveCentral(centralRegion, current.getPosition());

            // If current location is close to the end position, reconstruct and return the path.
            if (lngLatHandler.isCloseTo(current.getPosition(), endPosition)) {
                return reconstructPath(current);
            }

            // Add current location to closed set to avoid reprocessing it.
            closedSet.add(current.getPosition());

            ArrayList<Location> neighbours = getNeighbours(current, new Location(endPosition, null, 0, 0, 0), noFlyZones, centralRegion);
            for (Location neighbour : neighbours) {
                // Skip processing if this neighbour has already been evaluated.
                if (closedSet.contains(neighbour.getPosition())) {
                    continue;
                }

                // Add neighbour to open set for evaluation.
                openSet.add(neighbour);
            }
        }

        // Return an empty path if no path is found.
        return null;
    }



    private double hScore(LngLat position, LngLat endPosition) {
        return lngLatHandler.distanceTo(position, endPosition);
    }

    private double gScore(LngLat position, LngLat nextPosition) {
        return lngLatHandler.distanceTo(position, nextPosition);
    }

    private ArrayList<Location> getNeighbours(Location position, Location endPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        ArrayList<Location> neighbours = new ArrayList<>();

        for (double direction: DIRECTIONS) {
            LngLat currentPosition = position.getPosition();
            LngLat potentialNextPosition = lngLatHandler.nextPosition(currentPosition, direction);


            if (!legalMove(currentPosition, potentialNextPosition, noFlyZones, centralRegion)) {
                continue;
            }

            double gScore = gScore(currentPosition, potentialNextPosition);
            double hScore = hScore(potentialNextPosition, endPosition.getPosition());

            neighbours.add(new Location(potentialNextPosition, position, gScore, hScore, direction));
        }
        return neighbours;
    }

    private boolean legalMove(LngLat currentPosition, LngLat potentialPosition, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        for (NamedRegion noFlyZone: noFlyZones) {
            if (lngLatHandler.isInRegion(potentialPosition, noFlyZone)) {
                return false;
            }
        }

        boolean currentInCentral = lngLatHandler.isInRegion(currentPosition, centralRegion);
        boolean potentialInCentral = lngLatHandler.isInRegion(potentialPosition, centralRegion);

        return !currentInCentral || potentialInCentral || !leftCentralRegion;
    }

    private ArrayList<Double> reconstructPath(Location current) {
        ArrayList<Double> path = new ArrayList<>();
        while (current.getCameFrom() != null) {
            path.add(current.getAngle());
            current = current.getCameFrom();
        }
        Collections.reverse(path);
        return path;
    }

    private void canDroneLeaveCentral(NamedRegion centralRegion, LngLat dronePosition) {
        if (!leftCentralRegion && !lngLatHandler.isInRegion(dronePosition, centralRegion)) {
            leftCentralRegion = true;
        }
    }

}
