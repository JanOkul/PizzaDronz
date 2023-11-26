package uk.ac.ed.inf.Flight;

import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.OutputClasses.FlightPath;
import uk.ac.ed.inf.ilp.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Handles the calculation and formatting of flight data for drones.
 */
public class FlightDataHandler {
    private final LngLatHandler lngLatHandler = new LngLatHandler();
    private final LngLat START_POSITION = new LngLat(-3.186874, 55.944494);  // Appleton Tower.

    public FlightDataHandler() {
    }

    /**
     * Calculates the angles at which a drone must travel to complete a given order.
     * The method finds the restaurant associated with the order, calculates the path
     * to the restaurant, and then calculates the return path.
     *
     * @param order         The order for which to generate the flight path.
     * @param restaurants   An array of available restaurants.
     * @param noFlyZones    An array of areas where flying is prohibited.
     * @param centralRegion The designated central region for drone operations.
     * @return A list of angles representing the drone's path. Returns null if no path
     * can be found or if the restaurant is not found.
     */
    public ArrayList<Double> calculateAngles(Order order, Restaurant[] restaurants, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
        // Checks if any of the parameters are null.
        if (order == null || restaurants == null || noFlyZones == null || centralRegion == null) {
            System.err.println("FlightDataHandler - calculateAngles: retrieved null argument in + " + order + ", " +
                    Arrays.toString(restaurants) + ", " + Arrays.toString(noFlyZones) + ", " + centralRegion
                    + "\nreturning empty...");
            return new ArrayList<>();    // Handled in main class.
        }

        // Find restaurant position.
        Pizza targetPizza = order.getPizzasInOrder()[0];   // All pizzas come from the same restaurant.
        LngLat restaurantLocation = null;

        // Finds the restaurant that the pizza is from.
        for (Restaurant restaurant : restaurants) {
            List<Pizza> menu = Arrays.asList(restaurant.menu());
            if (menu.contains(targetPizza)) {
                restaurantLocation = restaurant.location();
                break;
            }
        }

        // If the restaurant is not found, return null such that main function can skip this order.
        if (restaurantLocation == null) {
            return new ArrayList<>();
        }

        // The anglePath is the list of angles, set it initially as the forward path.
        ArrayList<Double> anglePath = new PathGenerator().createFlightAngles(START_POSITION, restaurantLocation, noFlyZones, centralRegion);

        // If a path is not found, return null such that main function can skip this order.
        if (anglePath == null || anglePath.isEmpty()) {
            return new ArrayList<>();
        }

        // From restaurant to customer, since each move is constant distance, angles can be reversed.
        ArrayList<Double> returnAngles = reverseAngles(anglePath);

        // Adds in 999 to indicate hover move and the reversed angles.
        anglePath.add(999.0);
        anglePath.addAll(returnAngles);
        anglePath.add(999.0);

        return anglePath;
    }

    /**
     * Converts a list of angles into a list of flight path classes for a given order.
     * Each class represents a move from one point to the next at a specified angle.
     *
     * @param orderNo The order number to store within the flight path class.
     * @param angles  The list of angles representing the flight path, obtained from {@link #calculateAngles}.
     * @return An ArrayList of FlightPath objects, each representing a move of the overall path.
     */
    public ArrayList<FlightPath> convertAngleToFlightPath(String orderNo, ArrayList<Double> angles) {
        ArrayList<FlightPath> flightPath = new ArrayList<>();
        LngLat fromLngLat = START_POSITION;
        LngLat toLngLat;

        // For every angle in the list, calculate the next position and create a flight path object.
        for (Double angle : angles) {
            // A hover move will not move horizontally, so the next position is the same as the current position.
            toLngLat = angle == 999 ? fromLngLat : lngLatHandler.nextPosition(fromLngLat, angle);

            flightPath.add(new FlightPath(orderNo, fromLngLat, angle, toLngLat));
            fromLngLat = toLngLat;
        }
        return flightPath;
    }

    /**
     * Converts a list of angles into a list of LngLat classes for a given order.
     * Each class represents a move from one point to the next at a specified angle.
     *
     * @param angles The list of angles representing the flight path, obtained from {@link #calculateAngles}.
     * @return An ArrayList of LngLat objects, each representing a coordinate of the overall path.
     */
    public ArrayList<LngLat> convertAngleToList(ArrayList<Double> angles) {
        ArrayList<LngLat> flightList = new ArrayList<>();
        LngLat fromLngLat = START_POSITION;
        LngLat toLngLat;

        flightList.add(fromLngLat);

        for (Double angle : angles) {
            toLngLat = angle == 999 ? fromLngLat : lngLatHandler.nextPosition(fromLngLat, angle);

            flightList.add(toLngLat);
            fromLngLat = toLngLat;
        }
        return flightList;
    }

    /**
     * Reverses a given list of angles, mirroring the path.
     * This is used by {@link #calculateAngles} to calculate the return journey of a drone.
     *
     * @param angles The original list of angles.
     * @return An ArrayList of angles representing the reversed path.
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


