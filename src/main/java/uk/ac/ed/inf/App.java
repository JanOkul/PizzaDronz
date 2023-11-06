package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String api_url;
        LocalDate date;
        Order[] orders;
        Restaurant[] restaurants;
        NamedRegion[] no_fly_zones;
        NamedRegion central_area;
        boolean order_status_valid;
        boolean order_code_valid;
        OrderValidation validator = new OrderValidation();
        FlightPath flightPath = new FlightPath();
        RetrieveRestData retrieve_data = new RetrieveRestData();

        // Converts arguments into big string to display in error message.
        if (args.length != 2) {
            StringBuilder arg_string = new StringBuilder();
            // Append each argument to a StringBuilder.
            for (String arg : args) {
                arg_string.append(arg);
                arg_string.append(" ");
            }
            System.err.println("Expected 2 arguments: API URL, Date (YYYY-MM-DD), recieved " + args.length + " arguments: " + arg_string);
            return;
        }

        // Define arguments.
        api_url = args[0];
        date = LocalDate.parse(args[1]);  // Parse string argument into local date.

        // Retrieve data from REST API.
        orders          = retrieve_data.retrieveOrders(api_url, date);
        restaurants     = retrieve_data.retrieveRestaurants(api_url);
        no_fly_zones    = retrieve_data.retrieveNoFlyZones(api_url);
        central_area    = retrieve_data.retrieveCentralArea(api_url);

        // Validate all the orders retrieved.
        for (Order order: orders) {
            validator.validateOrder(order, restaurants);
        }

        long t1 = System.nanoTime();
        // Obtain flight path for all orders retrieved.
        for (Order order: orders) {
            order_status_valid = order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED;
            order_code_valid   = order.getOrderValidationCode() == OrderValidationCode.NO_ERROR;

            // Only get flight path of valid orders
            if (order_status_valid && order_code_valid) {
                flightPath.GenerateFlightPath(order, restaurants, no_fly_zones, central_area);
                System.out.println("order done");
            }
            else { System.out.println("error");}
        }
        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1_000_000);
    }
}
