package uk.ac.ed.inf.IO;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;

/**
 * Retrieves input data from a REST Sever.
 */
public class RetrieveRestData {

    public RetrieveRestData() {
    }

    /**
     * Retrieves Order data from the REST API.
     *
     * @param apiUrl    Rest API URL.
     * @return An array of the orders for a given day.
     */
    public Order[] retrieveOrders(String apiUrl) throws IOException {
        Order[] orders;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Tries to retrieve orders from REST API.
        try {
            URL url = new URL(apiUrl);
            orders = objectMapper.readValue(url, Order[].class);
        } catch (Exception e) {
            throw new IOException("RetrieveRestData: Failed to obtain orders" + e);
            }

        return orders;
    }

    /**
     * Retrieves Restaurant data from the REST API.
     *
     * @param apiUrl Rest API URL.
     * @return An array of all the restaurants.
     */
    public Restaurant[] retrieveRestaurants(String apiUrl) throws IOException {
        Restaurant[] restaurants;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Tries to retrieve restaurants from REST API.
        try {
            URL url = new URL(apiUrl);
            restaurants = objectMapper.readValue(url, Restaurant[].class);
        } catch (Exception e) {
            throw new IOException("RetrieveRestData: Failed to obtain restaurants" + e);
        }

        return restaurants;
    }

    /**
     * Retrieves NoFlyZone data from the REST API.
     *
     * @param apiUrl Rest API URL.
     * @return An array of the no-fly-zones.
     */
    public NamedRegion[] retrieveNoFlyZones(String apiUrl) throws IOException {
        NamedRegion[] noFlyZones;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Tries to retrieve noFlyZones from REST API.
        try {
            URL url = new URL(apiUrl);
            noFlyZones = objectMapper.readValue(url, NamedRegion[].class);
        } catch (Exception e) {
            throw new IOException("RetrieveRestData: Failed to obtain no-fly-zones" + e);
        }

        return noFlyZones;
    }


    /**
     * Retrieves the central area.
     *
     * @param apiUrl The URL of the Rest API.
     * @return The central area.
     */
    public NamedRegion retrieveCentralArea(String apiUrl) throws IOException {
        NamedRegion centralArea;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        // Tries to receive no-fly zones from REST API.
        try {
            URL url = new URL(apiUrl);
            centralArea = objectMapper.readValue(url, NamedRegion.class);
        } catch (Exception e) {
            throw new IOException("RetrieveRestData: Failed to obtain REST data for central area" + e);

        }

        return centralArea;
    }
}
