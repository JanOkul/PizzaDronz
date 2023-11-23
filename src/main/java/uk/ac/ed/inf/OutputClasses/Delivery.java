package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ed.inf.ilp.data.Order;

/**
 *  A reduced version of the Order class that has the data for deliveries.json.
 */
public class Delivery {
    private final String orderNo;
    private final String orderStatus;
    private final String orderValidationCode;
    private final int    costInPence;


    /**
     *  Assigns the data from Order to variables.
     * @param order The order that is being processed.
     */
    public Delivery(Order order) {
        orderNo              = order.getOrderNo();
        orderStatus          = order.getOrderStatus().toString();
        orderValidationCode  = order.getOrderValidationCode().toString();
        costInPence             = order.getPriceTotalInPence();
    }

    @JsonProperty
    public String getOrderNo() {
        return orderNo;
    }

    @JsonProperty
    public String getOrderStatus() {
        return orderStatus;
    }

    @JsonProperty
    public String getOrderValidationCode() {
        return orderValidationCode;
    }

    @JsonProperty
    public int getCostInPence() {
        return costInPence;
    }
}
