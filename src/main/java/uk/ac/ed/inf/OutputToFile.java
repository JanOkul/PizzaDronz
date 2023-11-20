package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
import java.time.LocalDate;


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
}
