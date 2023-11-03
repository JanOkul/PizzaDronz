package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.ed.inf.ilp.data.Order;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URL;


public class RetrieveOrders {
    /**
     *
     * @param api_url The URL of the Rest API
     * @return A list of Orders
     */
    public Order[] retrieveOrders(String api_url) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Order[0];
    }

    public static void main(String[] args) {
        RetrieveOrders retrieveOrders = new RetrieveOrders();
        retrieveOrders.retrieveOrders("https://ilp-rest.azurewebsites.net/orders/2023-11-03");
    }
}
