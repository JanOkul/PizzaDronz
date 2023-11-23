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


    public ArrayList<Double> generateFlightAngles(Order order, Restaurant[] restaurants, NamedRegion[] noFlyZones, NamedRegion centralRegion) {
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

        return anglePath;
    }

    public ArrayList<FlightPath> convertAngleToFlightPath(String orderNo, ArrayList<Double> angles) {
        ArrayList<FlightPath> flightPath = new ArrayList<>();
        LngLat fromLngLat = START_POSITION;
        LngLat toLngLat;

        // For every angle in the list, calculate the next position and create a flight path object.
        for (Double angle: angles) {
            // A hover move will not move horizontally, so the next position is the same as the current position.
            toLngLat = angle == 999 ? fromLngLat : lngLatHandler.nextPosition(fromLngLat, angle);

            flightPath.add(new FlightPath(orderNo, fromLngLat, angle, toLngLat));
            fromLngLat = toLngLat;
        }
        return flightPath;
    }

    public ArrayList<LngLat> convertAngleToList(ArrayList<Double> angles) {
        ArrayList<LngLat> flightList = new ArrayList<>();
        LngLat fromLngLat = START_POSITION;
        LngLat toLngLat;

        flightList.add(fromLngLat);

        for (Double angle: angles) {
            toLngLat = angle == 999 ? fromLngLat : lngLatHandler.nextPosition(fromLngLat, angle);

            flightList.add(toLngLat);
            fromLngLat = toLngLat;
        }
        return flightList;
    }
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


