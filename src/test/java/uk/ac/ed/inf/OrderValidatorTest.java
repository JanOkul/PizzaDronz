package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class OrderValidatorTest extends TestCase {

    private final Pizza pizza1 = new Pizza("a", 100);
    private final Pizza pizza2 = new Pizza("b", 100);
    private final Pizza[] samplePizza = new Pizza[]{pizza1, pizza2};

    private final DayOfWeek[] sampleDays = new DayOfWeek[]{
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
    };
    private final Restaurant sampleRestaurant = new Restaurant(
            "r",
            new LngLat(1.0, 1.0),
            sampleDays,
            samplePizza
    );

    private final String sampleCCNo = "0000000000000000";
    private final String sampleCCExpiry = "12/99";
    private final String sampleCCSecurityCode = "000";
    private final CreditCardInformation sampleCC = new CreditCardInformation(
            sampleCCNo,
            sampleCCExpiry,
            sampleCCSecurityCode
    );

    private final Order sampleOrder = new Order("0",
            LocalDate.now(),
            OrderStatus.UNDEFINED,
            OrderValidationCode.UNDEFINED,
             300,
            samplePizza,
            sampleCC
    );

    /**
     * Tests a valid order.
     */
    public void testValidateOrder() {
        OrderValidator validator = new OrderValidator();

        Order validatedOrder = validator.validateOrder(this.sampleOrder, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.NO_ERROR);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.VALID_BUT_NOT_DELIVERED);
    }

    /**
     * Tests a null order.
     */
    public void testValidateOrderWithNullOrder() {
        OrderValidator validator = new OrderValidator();

        try {
            validator.validateOrder(null, new Restaurant[]{sampleRestaurant});
            fail( "Missing exception" );
        } catch (NullPointerException e) {
            assertEquals("OrderValidator - validateOrder: Order to validate is null", e.getMessage());
        }

    }

    /**
     * Tests a null restaurant.
     */
    public void testValidateOrderWithNullRestaurants() {
        OrderValidator validator = new OrderValidator();

        try {
            validator.validateOrder(this.sampleOrder, null);
            fail( "Missing exception" );
        } catch (NullPointerException e) {
            assertEquals("OrderValidator - validateOrder: Defined restaurants are null", e.getMessage());
        }
    }

    /**
     * Tests result if more than maximum permitted pizzas.
     */
    public void testMoreThanFourPizzas() {
        OrderValidator validator = new OrderValidator();

        Pizza[] pizzas = new Pizza[]{pizza1, pizza1, pizza1, pizza1, pizza1};
        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                pizzas,
                sampleCC
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests result if no pizzas.
     */
    public void testNoPizzas() {
        OrderValidator validator = new OrderValidator();

        Pizza[] pizzas = new Pizza[]{};
        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                pizzas,
                sampleCC
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.UNDEFINED);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests an undefined pizza (one that is not defined within a restaurant menu).
     */
    public void testUndefinedPizza() {
        OrderValidator validator = new OrderValidator();

        Pizza[] pizzas = new Pizza[]{new Pizza("c", 100)};
        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                pizzas,
                sampleCC
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.PIZZA_NOT_DEFINED);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests a null pizza.
     */
    public void testNullPizza() {
        OrderValidator validator = new OrderValidator();

        Pizza[] pizzas = new Pizza[]{null};
        Order order = new Order("0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                pizzas,
                sampleCC
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.PIZZA_NOT_DEFINED);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests an order where the sum of the pizzas + 100 (delivery) is not the same as the stated total.
     */
    public void testInvalidTotalPrice() {
        OrderValidator validator = new OrderValidator();

        Order order = new Order("0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                100,
                samplePizza,
                sampleCC
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.TOTAL_INCORRECT);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests the result of the restaurant being closed
     */
    public void testClosedRestaurant() {
        OrderValidator validator = new OrderValidator();

        DayOfWeek[] noDays = new DayOfWeek[0];
        Restaurant closedRestaurant = new Restaurant("r", new LngLat(1.0, 1.0), noDays, samplePizza);

        Restaurant[] restaurants = new Restaurant[]{closedRestaurant};

        Order validatedOrder = validator.validateOrder(sampleOrder, restaurants);
        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.RESTAURANT_CLOSED);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests result of two pizzas from different restaurants.
     */
    public void testPizzaFromTwoRestaurants() {
        OrderValidator validator = new OrderValidator();

        Pizza[] menu1 = new Pizza[]{pizza1};
        Pizza[] menu2 = new Pizza[]{pizza2};
        Restaurant restaurant1 = new Restaurant("r1", new LngLat(1.0, 1.0), sampleDays, menu1);
        Restaurant restaurant2 = new Restaurant("r2", new LngLat(1.0, 1.0), sampleDays, menu2);

        Restaurant[] restaurants = new Restaurant[]{restaurant1, restaurant2};

        Pizza[] pizzas = new Pizza[]{pizza1, pizza2};

        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                pizzas,
                sampleCC
        );

        Order validatedOrder = validator.validateOrder(order, restaurants);

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests a credit card number with a character.
     */
    public void testCCWith1IllegalDigit() {
        OrderValidator validator = new OrderValidator();

        CreditCardInformation cc = new CreditCardInformation("A", sampleCCExpiry, sampleCCSecurityCode);

        Order order = new Order("0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                samplePizza,
                cc
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.CARD_NUMBER_INVALID);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests a CCV with a character.
     */
    public void testCCVWith1IllegalDigit() {
        OrderValidator validator = new OrderValidator();

        CreditCardInformation cc = new CreditCardInformation(sampleCCNo, sampleCCExpiry, "A");

        Order order = new Order("0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                samplePizza,
                cc
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.CVV_INVALID);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests an expiry date that is not a date.
     */
    public void testIllegalExpiry() {
        OrderValidator validator = new OrderValidator();

        CreditCardInformation cc = new CreditCardInformation(sampleCCNo, "A", sampleCCSecurityCode);

        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                samplePizza,
                cc
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.EXPIRY_DATE_INVALID);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests a card that expires on the 13th month, not possible.
     */
    public void testIllegalExpiry2() {
        OrderValidator validator = new OrderValidator();

        CreditCardInformation cc = new CreditCardInformation(sampleCCNo, "13/99", sampleCCSecurityCode);

        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                samplePizza,
                cc
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.EXPIRY_DATE_INVALID);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }

    /**
     * Tests a card that has expired.
     */
    public void testExpiredCard() {
        OrderValidator validator = new OrderValidator();

        CreditCardInformation cc = new CreditCardInformation(sampleCCNo, "01/00", sampleCCSecurityCode);

        Order order = new Order(
                "0",
                LocalDate.now(),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                300,
                samplePizza,
                cc
        );

        Order validatedOrder = validator.validateOrder(order, new Restaurant[]{sampleRestaurant});

        assertEquals(validatedOrder.getOrderValidationCode(), OrderValidationCode.EXPIRY_DATE_INVALID);
        assertEquals(validatedOrder.getOrderStatus(), OrderStatus.INVALID);
    }
}