package uk.ac.ed.inf.IO;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.OutputClasses.Delivery;
import uk.ac.ed.inf.OutputClasses.FeatureCollection;
import uk.ac.ed.inf.OutputClasses.FlightPath;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;


public class OutputToFile {

    public OutputToFile() {
    }

    /**
     * Creates a JSON file of the drones orders that were either delivered, or failed to be delivered.
     * @param orders An array of orders that the drone processed for that given day.
     */
    public void outputDeliveries(Order[] orders, LocalDate date) {
        ObjectMapper mapper = new ObjectMapper();
        Delivery[] deliveries = new Delivery[orders.length];

        // Converts each Order to a Delivery.
        for (int i = 0; i < orders.length; i++) {
            Delivery delivery = new Delivery(orders[i]);
            deliveries[i] = delivery;
        }

        try {
            mapper.writeValue(new File("deliveries-" + date.toString() + ".json"), deliveries);
        } catch (Exception e) {
            System.err.println("Failed to write deliveries to file: " + e);
            System.exit(1);
        }
    }

    public void outputFlightPaths(ArrayList<FlightPath> flightPaths, LocalDate date) {
        ObjectMapper mapper = new ObjectMapper();
        FlightPath[] flightPathsArray = new FlightPath[flightPaths.size()];
        flightPathsArray = flightPaths.toArray(flightPathsArray);

        try {
            mapper.writeValue(new File("flight-paths-" + date.toString() + ".json"), flightPathsArray);
        } catch (Exception e) {
            System.err.println("Failed to write flight paths to file: " + e);
            System.exit(1);
        }
    }

    public void outputGeoJson(FeatureCollection featureCollection, LocalDate date) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File("drone-" + date.toString() + ".geojson"), featureCollection);
        } catch (Exception e) {
            System.err.println("Failed to write flight paths to file: " + e);
            System.exit(1);
        }
    }

}
