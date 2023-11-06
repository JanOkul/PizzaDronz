package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.constant.*;

import java.awt.geom.Path2D;

public class LngLatHandling implements uk.ac.ed.inf.ilp.interfaces.LngLatHandling {

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

    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        double tolerance = SystemConstants.DRONE_IS_CLOSE_DISTANCE ;
        return distanceTo(startPosition, otherPosition) < tolerance;    // If distance is strictly less than tolerance then close.
    }

    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] points = region.vertices();
        int num_points = points.length;

        boolean in_region = false;

        for (int i = 0, j = num_points - 1; i < num_points; j = i++) {
            double xi = points[i].lng();
            double yi = points[i].lat();
            double xj = points[j].lng();
            double yj = points[j].lat();

            if (((yi > position.lat()) != (yj > position.lat())) &&
                    (position.lng() < (xj - xi) * (position.lat() - yi) / (yj - yi) + xi)) {
                in_region = !in_region;
            }
        }

        return in_region;
    }

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
