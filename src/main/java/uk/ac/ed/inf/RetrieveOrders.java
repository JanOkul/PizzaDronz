package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import com.google.gson.Gson;
import java.net.URL;


public class RetrieveOrders {
    /**
     *
     * @param api_url The URL of the Rest API
     * @return A list of Orders
     */
    public Order[] retrieveOrders(String api_url) {



        return null;
    }

    public static void main(String[] args) {
        RetrieveOrders retrieveOrders = new RetrieveOrders();
        retrieveOrders.retrieveOrders("https://ilp-rest.azurewebsites.net/orders/2023-11-02");
    }
}
