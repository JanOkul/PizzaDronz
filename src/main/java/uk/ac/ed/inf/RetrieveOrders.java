package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.net.URL;
import java.time.LocalDate;


public class RetrieveOrders {

    /**
     * Retrieves a list of the pizza orders for a particular date.
     * @param api_url The URL of the Rest API
     * @param date The date of the orders to retrieve
     * @return An array of Orders, if an error occurs, then an empty array is returned.
     */
    public static Order[] retrieveOrders(String api_url, LocalDate date) {
        String ORDER_URL = "orders/" + date;
        String IS_ALIVE_URL = "isAlive";
        Order[] orders;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!api_url.endsWith("/")){
            api_url += "/";
        }

        // Check if the REST API is alive.
        try {
            URL is_rest_alive = new URL(api_url + IS_ALIVE_URL);
            boolean alive = mapper.readValue(is_rest_alive , boolean.class);    // Maps JSON boolean to Java boolean.

            // If the REST API says it is not alive then create an error and return nothing.
            if (!alive) {
                System.err.println("The REST API is responding but not alive");
                return new Order[0];
            }
        } catch (Exception e) {
            return new Order[0];
        }

        // Tries to receive orders from REST API.
        try {
            URL orders_url = new URL(api_url + ORDER_URL);
            orders = mapper.readValue(orders_url, Order[].class);   // Maps JSON lines to Object classes.
        } catch (Exception e) {
            return new Order[0];
        }
        return orders;
    }

    /**
     * Retrieves a list restaurant data, which are open.
     * @param api_url The URL of the Rest API.
     * @return An array of Restaurant, if an error occurs, then an empty array is returned.
     */
    public static Restaurant[] retrieveRestaurants(String api_url) {
        String RESTAURANT_URL = "restaurants";
        String IS_ALIVE_URL = "isAlive";
        Restaurant[] restaurants;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!api_url.endsWith("/")){
            api_url += "/";
        }

        // Check if the REST API is alive.
        try {
            URL is_rest_alive = new URL(api_url + IS_ALIVE_URL);
            boolean alive = mapper.readValue(is_rest_alive , boolean.class);    // Maps JSON boolean to Java boolean.

            // If the REST API says it is not alive then create an error and return nothing.
            if (!alive) {
                System.err.println("The REST API is responding but not alive");
                return new Restaurant[0];
            }
        } catch (Exception e) {
            return new Restaurant[0];
        }

        // Tries to receive restaurants from REST API.
        try {
            URL restaurants_url = new URL(api_url + RESTAURANT_URL);
            System.out.println(restaurants_url);
            restaurants = mapper.readValue(restaurants_url, Restaurant[].class);   // Maps JSON lines to Restaurant classes.
        } catch (Exception e) {
            return new Restaurant[0];
        }
        return restaurants;
    }
}
