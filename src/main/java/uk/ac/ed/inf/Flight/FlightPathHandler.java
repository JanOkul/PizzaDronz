package uk.ac.ed.inf.Flight;

import uk.ac.ed.inf.OutputClasses.FlightPath;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.data.*;

import java.util.*;


public class FlightPathHandler {
    private final LngLatHandler LLHandler;

    public FlightPathHandler() {
        LLHandler = new LngLatHandler();
    }

    /**
     * The public method that finds the flight path for an order.
     *
     * @param order          The order to generate a flight path for.
     * @param restaurants    List of currently supported restaurants.
     * @param noFlyZones   A list of zones the drone cannot fly in.
     * @param centralRegion The region of the UoE central area.
     */


    public FlightPath[] GenerateFlightPath(Order order, Restaurant[] restaurants, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        LngLat startPosition = new LngLat((-3.186874), 55.944494);

        // Find restaurant position.
        Pizza targetPizza = order.getPizzasInOrder()[0];   // All pizzas come from the same restaurant.
        LngLat restaurantLocation = null;

        // Finds the restaurant that the pizza is from.
        for (Restaurant restaurant : restaurants) {
            ArrayList<Pizza> menu = new ArrayList<>(Arrays.asList(restaurant.menu()));
            if (menu.contains(targetPizza)) {
                restaurantLocation = restaurant.location();
                break;
            }
        }

        // Create the angles for the flight path.
        ArrayList<Double> anglePath = new PathGenerator().createFlightPath(startPosition, restaurantLocation, noFlyZones, centralRegion);
        ArrayList<Double> returnAngles = reverseAngles(anglePath);

        // Create the full angle path
        anglePath.add(999.0);
        anglePath.addAll(returnAngles);
        anglePath.add(999.0);

        return convertAngleToFlightPath(order.getOrderNo(), startPosition, anglePath);
    }





    /**
     * Converts the list of angles created by createFlightPath into a list of flight paths.
     * @param orderNo The order number.
     * @param startPosition The starting position of the drone.
     * @param angles An arraylist of the angles of the moves the drone will take.
     * @return An array of flight path.
     */
    private FlightPath[] convertAngleToFlightPath(String orderNo, LngLat startPosition, ArrayList<Double> angles) {
        LngLat fromLngLat = startPosition;
        LngLat toLngLat;
        int size = angles.size();
        FlightPath[] flightPath = new FlightPath[size];

        for (int i=0; i<size; i++) {
            double angle = angles.get(i);
            if (angle == 999) {
                toLngLat = fromLngLat;
            } else {
                toLngLat = LLHandler.nextPosition(fromLngLat, angle);
            }
            flightPath[i] = new FlightPath(orderNo, fromLngLat, angle, toLngLat);
            fromLngLat = toLngLat;
        }
        return flightPath;
    }

    /**
     * Reverses the angles created by createFlightPath, for the return path home. Since angles are flipped
     * each angle is has 180 added to it.
     * @param angles An arraylist of the angles of the moves the drone will take.
     * @return An arraylist of the angles of the moves the drone will take in reverse.
     */
    private ArrayList<Double> reverseAngles(ArrayList<Double> angles) {
        int size = angles.size();
        for (int i=0; i<size; i++) {
            double angle = angles.get(i);
            if (angle != 999) {
                angles.set(i, angle + 180);
            }
        }
        Collections.reverse(angles);
        return angles;
    }
}


