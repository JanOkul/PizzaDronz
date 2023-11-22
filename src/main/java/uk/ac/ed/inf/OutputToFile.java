package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.ilp.data.LngLat;
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

    public void outputFlightPaths(String order_no, double angle, LngLat[] flight_paths, LocalDate date) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<FlightPath> flight_path_classes = new ArrayList<>();

        for (int i = 0; i < flight_paths.length-1; i++) {
            int j = i+1;
            flight_path_classes.add(new FlightPath(order_no, flight_paths[i], angle , flight_paths[j]));

        }

        try {
            mapper.writeValue(new File("flightpath-" + date.toString() + ".json"), flight_path_classes);
        } catch (Exception e) {
            System.err.println("Failed to write deliveries to file: " + e);
            System.exit(1);
        }
    }
}
