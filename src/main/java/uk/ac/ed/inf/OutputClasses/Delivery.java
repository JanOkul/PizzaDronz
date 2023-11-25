package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ed.inf.ilp.data.Order;

/**
 * A reduced version of the Order class that has the data for deliveries.json.
 */
public class Delivery {
    @JsonProperty
    private final String orderNo;
    @JsonProperty
    private final String orderStatus;
    @JsonProperty
    private final String orderValidationCode;
    @JsonProperty
    private final int costInPence;


    /**
     * Assigns the data from Order to variables.
     *
     * @param order The order that is being processed.
     */
    public Delivery(Order order) {
        orderNo = order.getOrderNo();
        orderStatus = order.getOrderStatus().toString();
        orderValidationCode = order.getOrderValidationCode().toString();
        costInPence = order.getPriceTotalInPence();
    }
}
