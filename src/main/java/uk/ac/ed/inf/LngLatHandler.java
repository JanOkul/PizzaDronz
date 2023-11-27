package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.constant.*;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        double lng1, lng2, lat1, lat2, distance;
        // If the position is ever greater, flip to the other side of the world.
        lng1 = startPosition.lng();
        lng2 = endPosition.lng();
        lat1 = startPosition.lat();
        lat2 = endPosition.lat();

        // Distance Formula
        distance = Math.sqrt(Math.pow(lng2 - lng1, 2) + Math.pow(lat2 - lat1, 2));
        /*
            Due to how doubles work, if the is distance to is just at 1.5e-4 the distance will be 1.4999999999999998
            which is not equal to 1.5e-4. This causes isCloseTo to return true instead of false. So we round to 5
            places.
         */
        BigDecimal bd = new BigDecimal(distance);
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
        int intersections = 0;
        LngLat[] vertices = region.vertices();
        int numVertices = vertices.length;

        // If point is on corner of region, return true.
        for (LngLat vertex : vertices) {
            if (point.lat() == vertex.lat() && point.lng() == vertex.lng()) {
                return true;
            }
        }

        // Ray casting algorithm.
        int j = numVertices - 1;
        // For each side in region.
        for (int i = 0; i < numVertices; i++) {
            // One side
            LngLat v1 = vertices[i];
            LngLat v2 = vertices[j];

            boolean isPointHigherThanSide = ((v1.lat() > point.lat()) != (v2.lat() > point.lat()));
            boolean isPointLeftOfSide = (point.lng() < (v2.lng() - v1.lng()) * (point.lat() - v1.lat()) / (v2.lat() - v1.lat()) + v1.lng());

            if (isPointHigherThanSide && isPointLeftOfSide) {
                intersections++;
            }
            j = i;
        }

        return intersections % 2 != 0;
    }


    /**
     * Calculates the next position given a starting position and an angle.
     *
     * @param startPosition The starting position as longitude and latitude.
     * @param angle         The angle to move in.
     * @return The next position as longitude and latitude.
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        double lng1, lng2, lat1, lat2;
        lng1 = startPosition.lng();
        lat1 = startPosition.lat();
        angle = Math.toRadians(angle);  // Java sin and cos only take radians.

        // Calculates new x and y positions.
        lng2 = lng1 + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angle));
        lat2 = lat1 + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angle));
        return new LngLat(lng2, lat2);
    }

}
