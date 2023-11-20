package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.net.URL;
import java.time.LocalDate;


public class RetrieveRestData {

    public RetrieveRestData() {
    }

    /**
     * Retrieves a list of the pizza orders for a particular date.
     * @param api_url The URL of the Rest API
     * @param date The date of the orders to retrieve
     * @return An array of Orders, if an error occurs, then an empty array is returned.
     */
    public Order[] retrieveOrders(String api_url, LocalDate date) {
        String ORDER_URL = "orders/" + date;
        Order[] orders = new Order[0];

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!api_url.endsWith("/")){
            api_url += "/";
        }

        // Tries to receive orders from REST API.
        try {
            URL orders_url = new URL(api_url + ORDER_URL);
            orders = mapper.readValue(orders_url, Order[].class);   // Maps JSON lines to Object classes.
        }  catch (Exception e) {
            System.err.println("Failed to obtain orders: " + e);
            System.exit(1);
        }
        return orders;
    }

    /**
     * Retrieves a list restaurant data, which are open.
     * @param api_url The URL of the Rest API.
     * @return An array of Restaurant, if an error occurs, then an empty array is returned.
     */
    public Restaurant[] retrieveRestaurants(String api_url) {
        String RESTAURANT_URL = "restaurants";
        Restaurant[] restaurants = new Restaurant[0];

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!api_url.endsWith("/")){
            api_url += "/";
        }

        // Tries to receive restaurants from REST API.
        try {
            URL restaurants_url = new URL(api_url + RESTAURANT_URL);
            restaurants = mapper.readValue(restaurants_url, Restaurant[].class);   // Maps JSON lines to Restaurant classes.
        } catch (Exception e) {
            System.err.println("Failed to obtain restaurants: " + e);
            System.exit(1);
        }
        return restaurants;
    }

    /**
     * Retrieves a list of no-fly zones.
     * @param api_url The URL of the Rest API.
     * @return An array of NamedRegion, if an error occurs, then an empty array is returned.
     */
    public NamedRegion[] retrieveNoFlyZones(String api_url) {
        String NOFLYZONE_URL = "noFlyZones";
        NamedRegion[] no_fly_zones = new NamedRegion[0];

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!api_url.endsWith("/")) {
            api_url += "/";
        }

        // Tries to receive no-fly zones from REST API.
        try {
            URL noflyzone_url = new URL(api_url + NOFLYZONE_URL);
            no_fly_zones = mapper.readValue(noflyzone_url, NamedRegion[].class);   // Maps JSON lines to NamedRegion classes.
        }  catch (Exception e) {
            System.err.println("Failed to obtain no-fly-zones: " + e);
            System.exit(1);
        }

        return no_fly_zones;
    }

        /**
         * Retrieves the central area.
         * @param api_url The URL of the Rest API.
         * @return A NamedRegion, if an error occurs, then null is returned.
         */
        public NamedRegion retrieveCentralArea(String api_url) {
            String CentralArea_URL = "centralArea";
            NamedRegion central_area_zones = null;

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // Adds a slash to end of domain if it is not there.
            if (!api_url.endsWith("/")){
                api_url += "/";
            }

            // Tries to receive no-fly zones from REST API.
            try {
                URL central_area_url = new URL(api_url + CentralArea_URL);
                central_area_zones = mapper.readValue(central_area_url, NamedRegion.class);   // Maps JSON lines to NamedRegion classes.
            } catch (Exception e) {
                System.err.println("Failed to obtain central area: " + e);
                System.exit(1);
            }

            if (central_area_zones == null) {
                System.err.println("Obtained central area, however central area is null");
                System.exit(1);
            }

            return central_area_zones;
    }
}
