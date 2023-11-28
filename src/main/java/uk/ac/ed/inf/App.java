package uk.ac.ed.inf;

import uk.ac.ed.inf.Flight.FlightDataHandler;
import uk.ac.ed.inf.IO.OutputToFile;
import uk.ac.ed.inf.IO.RetrieveRestData;
import uk.ac.ed.inf.OutputClasses.Feature;
import uk.ac.ed.inf.OutputClasses.FeatureCollection;
import uk.ac.ed.inf.OutputClasses.FlightPath;
import uk.ac.ed.inf.OutputClasses.Geometry;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * The main class of the Drone.
 * The program takes in the url of the API and a date to fetch orders for.
 * The program will retrieve the orders, restaurants, no-fly zones and central area from the API.
 * The program will validate, then calculate the flight path for each order, and finally will output data to files.
 */
public class App {
    /**
     * The main loop of the Drone.
     *
     * @param args Only 2 arguments url and date are accepted.
     */
    public static void main(String[] args) {
        OrderValidator validator = new OrderValidator();
        FlightDataHandler flightDataHandler = new FlightDataHandler();
        RetrieveRestData retrieve_data = new RetrieveRestData();

        // Check if the correct number of arguments is passed
        if (args.length != 2) {
            StringBuilder argsAsString = new StringBuilder();
            for (String arg : args) {
                argsAsString.append(arg).append(" ");
            }
            System.err.println("Expected 2 arguments: API URL, Date (YYYY-MM-DD), received " + args.length
                    + " arguments: " + argsAsString + ", exiting...");
            System.exit(1);
        }

        // Define arguments
        String apiUrl = args[0];
        LocalDate date = null;
        try {
            date = LocalDate.parse(args[1]);
        } catch (DateTimeParseException e) {
            System.err.println("Date is not in the correct format, needs to be \"YYYY-MM-DD\": " + e + ", exiting...");
            System.exit(1);
        }

        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        // Retrieve data from REST API
        Order[] orders;
        Restaurant[] restaurants;
        NamedRegion[] noFlyZones;
        NamedRegion centralArea;

        try {
            orders = retrieve_data.retrieveOrders(apiUrl + "orders/" + date.toString());
            restaurants = retrieve_data.retrieveRestaurants(apiUrl + "restaurants");
            noFlyZones = retrieve_data.retrieveNoFlyZones(apiUrl + "noFlyZones");
            centralArea = retrieve_data.retrieveCentralArea(apiUrl + "centralArea");
        } catch (IOException e) {
            System.err.println("Main: Failed to retrieve data from REST API, " + e + ", exiting...");
            System.exit(1);
            return;
        }

        // ArrayLists for flight data.
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        ArrayList<FlightPath> flightPaths = new ArrayList<>();  // Flight paths in the json output class.
        ArrayList<LngLat> lngLats = new ArrayList<>();  // Flight path in the LngLat class. (for geo-json)
        ArrayList<Double> angles;   // The angles the drone takes from move to move.

        // Main loop of operation for each order.
        for (Order order : orders) {
            try {
                validator.validateOrder(order, restaurants);
            } catch (NullPointerException e) {
                System.err.println("Main: Order or restaurants are null, skipping order...");
                continue;
            }

            boolean order_status_valid = order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED;
            boolean order_code_valid = order.getOrderValidationCode() == OrderValidationCode.NO_ERROR;

            // Only get flight path of valid orders
            if (order_status_valid && order_code_valid) {
                try {
                    angles = flightDataHandler.calculateAngles(order, restaurants, noFlyZones, centralArea, appletonTower);
                } catch (IllegalArgumentException e) {
                    System.err.println("Main: Failed to calculate angles for order: " + order.getOrderNo() + ", " +
                            e.getMessage() + ", skipping order...");
                    continue;
                }
                // If there is an error with finding a path, continue to next order.
                if (angles == null || angles.isEmpty()) {
                    System.err.println("Main: No path found for order: " + order.getOrderNo() + ", skipping order...");
                    continue;
                }
                order.setOrderStatus(OrderStatus.DELIVERED);

                // Adds data into arraylists for their output type.
                flightPaths.addAll(flightDataHandler.convertAngleToFlightPath(order.getOrderNo(), angles, appletonTower));
                lngLats.addAll(flightDataHandler.convertAngleToList(angles, appletonTower));
            }

        }

        // Output flight path to file
        // Create GeoJson
        Geometry geometry = new Geometry();

        // Adds coordinates to the line string.
        for (LngLat lngLat : lngLats) {
            ArrayList<Double> coordinates = new ArrayList<>();
            coordinates.add(lngLat.lng());
            coordinates.add(lngLat.lat());
            geometry.addCoordinates(coordinates);
        }

        // Creates a feature and feature collection.
        Feature feature = new Feature(geometry);
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.addFeature(feature);

        // Tries to output all the data.
        OutputToFile output = new OutputToFile();
        try {
            output.outputDeliveries(orders, date);
            output.outputFlightPaths(flightPaths, date);
            output.outputGeoJson(featureCollection, date);
        } catch (IOException e) {
            System.err.println("Main: Failed to output deliveries or flight paths, " + e + ", exiting...");
            System.exit(1);
        }
    }

}