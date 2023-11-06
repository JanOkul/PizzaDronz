package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
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
        NamedRegion[] no_fly_zones = RetrieveRestData.retrieveNoFlyZones(api_url);
        NamedRegion central_area = RetrieveRestData.retrieveCentralArea(api_url);

        OrderValidation validator = new OrderValidation();
        FlightPath flightPath = new FlightPath();
//         Validate all the orders retrieved.
//        for (Order order: orders) {
//            validator.validateOrder(order, restaurants);
//
//            if (order.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)) {
//                flightPath.GenerateFlightPath(order, restaurants, no_fly_zones, central_area);
//            }
//        }

        Order test = validator.validateOrder(orders[0], restaurants);
        long t1 = System.nanoTime();
        LngLat[] test2 = flightPath.GenerateFlightPath(test, restaurants, no_fly_zones, central_area);
        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1_000_000);
        for (LngLat l: test2) {
            System.out.print(l.lng());
            System.out.print(" ");
            System.out.print(l.lat());
            System.out.print("\n");
        }
        System.out.println( new LngLatHandling().isCloseTo(new LngLat((-3.1912869215011597), 55.945535152517735), new LngLat ((-3.191297269045236), 55.945461710882974 )));


    }
}
