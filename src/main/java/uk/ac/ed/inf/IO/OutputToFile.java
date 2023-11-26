package uk.ac.ed.inf.IO;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.OutputClasses.Delivery;
import uk.ac.ed.inf.OutputClasses.FeatureCollection;
import uk.ac.ed.inf.OutputClasses.FlightPath;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;


public class OutputToFile {

    public OutputToFile() {
    }

    /**
     * Creates a JSON file of the drones orders that were either delivered, or failed to be delivered.
     *
     * @param orders An ar array of the orders that were processed.
     * @param date   The given date the orders were made.
     */
    public void outputDeliveries(Order[] orders, LocalDate date) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Delivery[] deliveries = new Delivery[orders.length];

        // Converts each Order to a Delivery.
        for (int i = 0; i < orders.length; i++) {
            Delivery delivery = new Delivery(orders[i]);
            deliveries[i] = delivery;
        }

        // Tries to write deliveries to a JSON file.
        try {
            mapper.writeValue(new File("deliveries-" + date.toString() + ".json"), deliveries);
        } catch (Exception e) {
            throw new IOException("OutputToFile - outputDeliveries: Failed to write deliveries to file: ");
        }
    }

    /**
     * Outputs each move to move with the angle and order number in to a JSON file for each date.
     *
     * @param flightPaths An ArrayList of the FlightPath class that stores each move.
     * @param date        The date the orders was made on.
     */
    public void outputFlightPaths(ArrayList<FlightPath> flightPaths, LocalDate date) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Tries to write flight paths to a JSON file.
        try {
            mapper.writeValue(new File("flightpath-" + date.toString() + ".json"), flightPaths);
        } catch (Exception e) {
            throw new IOException("OutputToFile - outputFlightPaths: Failed to write flight paths to file: ");
        }
    }

    /**
     * Outputs a FeatureCollection with flight path information to a GEO JSON file.
     *
     * @param featureCollection The Feature Collection that holds all the coordinates.
     * @param date              The date the orders were made on.
     */
    public void outputGeoJson(FeatureCollection featureCollection, LocalDate date) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Tries to write Feature Collection to a GEO JSON.
        try {
            mapper.writeValue(new File("drone-" + date.toString() + ".geojson"), featureCollection);
        } catch (Exception e) {
            throw new IOException("OutputToFile - outputGeoJson: Failed to write flight paths to file: " + e);
        }
    }

}
