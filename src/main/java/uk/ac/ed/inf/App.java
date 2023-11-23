package uk.ac.ed.inf;

import uk.ac.ed.inf.Flight.FlightPathHandler;
import uk.ac.ed.inf.IO.OutputToFile;
import uk.ac.ed.inf.IO.RetrieveRestData;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        long t1 = System.nanoTime();
        OrderValidator validator        = new OrderValidator();
        FlightPathHandler flightPathHandler = new FlightPathHandler();
        RetrieveRestData retrieve_data  = new RetrieveRestData();

        // Check if the correct number of arguments is passed
        if (args.length != 2) {
            StringBuilder arg_string = new StringBuilder();
            for (String arg : args) {
                arg_string.append(arg).append(" ");
            }
            System.err.println("Expected 2 arguments: API URL, Date (YYYY-MM-DD), received " + args.length + " arguments: " + arg_string);
            System.exit(1);
        }

        // Define arguments
        String api_url = args[0];
        LocalDate date = LocalDate.parse(args[1]);

        // Retrieve data from REST API
        Order[] orders             = retrieve_data.retrieveData(api_url, "orders/" + date,  Order.class);
        Restaurant[] restaurants   = retrieve_data.retrieveData(api_url, "restaurants",  Restaurant.class);
        NamedRegion[] no_fly_zones = retrieve_data.retrieveData(api_url, "noFlyZones",  NamedRegion.class);
        NamedRegion central_area   = retrieve_data.retrieveCentralArea(api_url, "centralArea");
        System.out.println(central_area);
        // Validate all the orders retrieved
        for (Order order : orders) {
            System.out.println(order.getOrderNo());
            validator.validateOrder(order, restaurants);
        }

        // Obtain flight path for all orders retrieved
        for (Order order : orders) {
            boolean order_status_valid = order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED;
            boolean order_code_valid = order.getOrderValidationCode() == OrderValidationCode.NO_ERROR;

            // Only get flight path of valid orders
            if (order_status_valid && order_code_valid) {
                System.out.println(Arrays.toString(flightPathHandler.GenerateFlightPath(order, restaurants, no_fly_zones, central_area)));
                System.out.println();
            }
        }

        // Output deliveries to file
        OutputToFile output = new OutputToFile();
        output.outputDeliveries(orders, date);
        long t2 = System.nanoTime();
        System.out.println((t2 - t1) / 1_000_000);
    }
}
