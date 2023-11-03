package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
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
        String api_url = args[1];
        LocalDate date = LocalDate.parse(args[0]);  // Parse string argument into local date.

        // Retrieve data from REST API.
        Order[] orders = RetrieveRestData.retrieveOrders(api_url, date);
        Restaurant[] restaurants = RetrieveRestData.retrieveRestaurants(api_url);

        OrderValidation validator = new OrderValidation();
        FlightPath flightPath = new FlightPath();

        // Validate all the orders retrieved.
        for (Order order: orders) {
            validator.validateOrder(order, restaurants);

            if (order.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)) {
                flightPath.GenerateFlightPath(order, restaurants);
            }
        }


        System.out.println(orders.length);
        System.out.println(restaurants.length);
    }
}
