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
        // Using a path object as it has a contains method which will tell if drone is in region (the path).
        Path2D.Double polygon = new Path2D.Double();

        polygon.moveTo(points[0].lng(), points[0].lat());   // Start path from first point in list.
        // Draw a line from previous point to current point.
        for (int point_index = 1; point_index < points.length; point_index++) {
            polygon.lineTo(points[point_index].lng(), points[point_index].lat());
        }
        polygon.closePath();    // Draws final line back to original point.
        return polygon.contains(position.lng(), position.lat());
    }

    public LngLat nextPosition(LngLat startPosition, double angle) {
        double x_1, y_1, x_2, y_2;
        x_1 = startPosition.lng();
        y_1 = startPosition.lat();
        angle = Math.toRadians(angle);  // Java sin and cos only take radians.

        // Calculates new x and y positions.
        x_2 = x_1 + Math.acos(angle);
        y_2 = y_1 + Math.asin(angle);
        return new LngLat(x_2, y_2);
    }
}
