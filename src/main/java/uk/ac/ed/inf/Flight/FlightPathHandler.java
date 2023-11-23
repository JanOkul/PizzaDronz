package uk.ac.ed.inf.Flight;

import uk.ac.ed.inf.OutputClasses.FlightPath;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.data.*;

import java.util.*;


public class FlightPathHandler {
    private final LngLatHandler lngLatHandler = new LngLatHandler();
    private final LngLat START_POSITION = new LngLat((-3.186874), 55.944494);  // Appleton Tower.

    public FlightPathHandler() {
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

        // From Appleton Tower to restaurant.
        ArrayList<Double> anglePath = new PathGenerator().createFlightAngles(START_POSITION, restaurantLocation, noFlyZones, centralRegion);

        // If a path is not found, return null such that main function can skip this order.
        if (anglePath.size() == 0) {
            return null;
        }

        // From restaurant to customer, since each move is constant distance, angles can be reversed.
        ArrayList<Double> returnAngles = reverseAngles(anglePath);

        // Adds in 999 to indicate hover move and the reversed angles.
        anglePath.add(999.0);
        anglePath.addAll(returnAngles);
        anglePath.add(999.0);

        // Converts angles to FlightPath objects.
        return convertAngleToFlightPath(order.getOrderNo(), START_POSITION, anglePath);
    }

    /**
     * Converts the list of angles created by createFlightAngles into a list of flight paths.
     * @param orderNo The order number.
     * @param startPosition The starting position of the drone.
     * @param angles An arraylist of the angles of the moves the drone will take.
     * @return An array of flight path.
     */
    private FlightPath[] convertAngleToFlightPath(String orderNo, LngLat startPosition, ArrayList<Double> angles) {
        int size = angles.size();
        FlightPath[] flightPath = new FlightPath[size];
        LngLat fromLngLat = startPosition;
        LngLat toLngLat;

        // For every angle in the list, calculate the next position and create a flight path object.
        for (int i = 0; i < size; i++) {
            double angle = angles.get(i);
            // A hover move will not move horizontally, so the next position is the same as the current position.
            if (angle == 999) {
                toLngLat = fromLngLat;
            } else {
                toLngLat = lngLatHandler.nextPosition(fromLngLat, angle);
            }
            flightPath[i] = new FlightPath(orderNo, fromLngLat, angle, toLngLat);
            fromLngLat = toLngLat;
        }
        return flightPath;
    }

    /**
     * Reverses the angles created by createFlightAngles, for the return path home. Since angles are flipped
     * each angle is has 180 added to it.
     * @param angles An arraylist of the angles of the moves the drone will take.
     * @return An arraylist of the angles of the moves the drone will take in reverse.
     */
    private ArrayList<Double> reverseAngles(ArrayList<Double> angles) {
        ArrayList<Double> reversed_angles = new ArrayList<>();

        // Does a 180 degree flip for each angle
        for (double angle : angles) {
            reversed_angles.add(angle + 180);
        }
        // Reverses the order.
        Collections.reverse(reversed_angles);
        return reversed_angles;
    }
}


