package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.constant.*;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

public class LngLatHandler implements LngLatHandling {

    public LngLatHandler() {
    }

    /**
     * Calculates the distance between two points.
     * @param startPosition The starting position as longitude and latitude.
     * @param endPosition   The ending position as longitude and latitude.
     * @return The distance between the two points in degrees.
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double x_1, x_2, y_1, y_2, distance;
        x_1 = startPosition.lng();
        y_1 = startPosition.lat();
        x_2 = endPosition.lng();
        y_2 = endPosition.lat();
        // Distance Formula
        distance = Math.sqrt(Math.pow(x_2-x_1, 2) + Math.pow(y_2-y_1, 2));
        return distance;
    }

    /**
     * Checks if two points are close to each other.
     * @param startPosition The starting position as longitude and latitude.
     * @param otherPosition The ending position as longitude and latitude.
     * @return True if the two points are close to each other, false otherwise.
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        double tolerance = SystemConstants.DRONE_IS_CLOSE_DISTANCE ;
        return distanceTo(startPosition, otherPosition) < tolerance;
    }

    /**
     * Checks if a point is in a region by using ray casting.
     * @param position The position to check.
     * @param region The region to check.
     * @return True if the point is in the region, false otherwise.
     */
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] points = region.vertices();
        int num_points = points.length;

        boolean in_region = false;

        for (int i = 0, j = num_points - 1; i < num_points; j = i++) {
            double xi = points[i].lng();
            double yi = points[i].lat();
            double xj = points[j].lng();
            double yj = points[j].lat();

            // Checks if the point is between two vertices.
            if (((yi > position.lat()) != (yj > position.lat())) && (position.lng() < (xj - xi) * (position.lat() - yi) / (yj - yi) + xi)) {
                in_region = !in_region;
            }
        }
        return in_region;
    }

    /**
     * Calculates the next position given a starting position and an angle.
     * @param startPosition The starting position as longitude and latitude.
     * @param angle The angle to move in.
     * @return The next position as longitude and latitude.
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        double x_1, y_1, x_2, y_2;
        x_1 = startPosition.lng();
        y_1 = startPosition.lat();
        angle = Math.toRadians(angle);  // Java sin and cos only take radians.

        // Calculates new x and y positions.
        x_2 = x_1 + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angle));
        y_2 = y_1 + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angle));
        return new LngLat(x_2, y_2);
    }
}
