package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.constant.*;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

/**
 * Performs important operations with the LngLat class for finding the flight path of the drone.
 */
public class LngLatHandler implements LngLatHandling {

    public LngLatHandler() {
    }

    /**
     * Calculates the distance between two points.
     *
     * @param startPosition The starting position as longitude and latitude.
     * @param endPosition   The ending position as longitude and latitude.
     * @return The distance between the two points in degrees.
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double x1, x2, y1, y2, distance;
        x1 = startPosition.lng();
        y1 = startPosition.lat();
        x2 = endPosition.lng();
        y2 = endPosition.lat();
        // Distance Formula
        distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return distance;
    }

    /**
     * Checks if two points are close to each other.
     *
     * @param startPosition The starting position as longitude and latitude.
     * @param otherPosition The ending position as longitude and latitude.
     * @return True if the two points are close to each other, false otherwise.
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        double tolerance = SystemConstants.DRONE_IS_CLOSE_DISTANCE;
        return distanceTo(startPosition, otherPosition) < tolerance;
    }

    /**
     * Checks if a point is in a region by using ray casting.
     *
     * @param point  The position to check.
     * @param region The region to check.
     * @return True if the point is in the region, false otherwise.
     */
    public boolean isInRegion(LngLat point, NamedRegion region) {
        boolean isInside = false;
        LngLat[] vertices = region.vertices();
        int verticesLength = vertices.length;

        for (int i = 0, j = verticesLength - 1; i < verticesLength; j = i++) {
            LngLat vertex1 = vertices[i];
            LngLat vertex2 = vertices[j];

            // Check if the point's latitude is between the latitudes of the edge's vertices
            boolean isBetweenLatitudes = (vertex1.lat() > point.lat()) != (vertex2.lat() > point.lat());

            // Check if the point's longitude is to the left of the edge
            if (isBetweenLatitudes) {
                double edgeSlope = (vertex2.lng() - vertex1.lng()) / (vertex2.lat() - vertex1.lat());
                double intersectLng = vertex1.lng() + (point.lat() - vertex1.lat()) * edgeSlope;

                if (point.lng() < intersectLng) {
                    isInside = !isInside;
                }
            }
        }

        return isInside;
    }


    /**
     * Calculates the next position given a starting position and an angle.
     *
     * @param startPosition The starting position as longitude and latitude.
     * @param angle         The angle to move in.
     * @return The next position as longitude and latitude.
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        double x1, x2, y1, y2;
        x1 = startPosition.lng();
        y1 = startPosition.lat();
        angle = Math.toRadians(angle);  // Java sin and cos only take radians.

        // Calculates new x and y positions.
        x2 = x1 + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angle));
        y2 = y1 + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angle));
        return new LngLat(x2, y2);
    }

}
