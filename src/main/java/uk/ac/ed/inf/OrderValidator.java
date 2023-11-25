package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Ensues that a given order is valid so no errors occur when trying to deliver orders.
 */
public class OrderValidator implements OrderValidation {

    public OrderValidator() {
    }

    /**
     * Validates an order and returns the order with the correct status and validation code.
     *
     * @param orderToValidate    The order to validate.
     * @param definedRestaurants The restaurants that are currently supported.
     * @return The order with the correct status and validation code.
     */
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        if (orderToValidate == null) {
            return null;    // Handled in a main class.
        }

        if (definedRestaurants == null) {
            System.err.println("Order validation: defined restaurants is null");
            System.exit(1);
        }

        // -------------------- PIZZA CHECKS --------------------
        Pizza[] orderedPizzas = orderToValidate.getPizzasInOrder();
        // ---------- Checks if too many pizzas have been sent ----------
        if (orderedPizzas.length > 4) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        // ---------- Checks if no pizzas have been ordered ----------
        if (orderedPizzas.length < 1) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            return orderToValidate;
        }

        // ---------- Checks for undefined pizzas -----------
        boolean invalidPizza = false;

        // Checks if the pizzas ordered are on the menu of any restaurant
        for (Pizza pizza : orderedPizzas) {
            // Checks if any pizza is null
            if (pizza == null) {
                invalidPizza = true;
                break;
            }

            // Checks if pizzas are in any menu.
            boolean isPizzaInAnyMenu = false;
            for (Restaurant restaurant : definedRestaurants) {
                if (Arrays.asList(restaurant.menu()).contains(pizza)) {
                    isPizzaInAnyMenu = true;
                    break;
                }
            }
            if (!isPizzaInAnyMenu) {
                invalidPizza = true;
                break;
            }
        }

        if (invalidPizza) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        // ---------- Checks if pizza price is accurate ----------
        int pizzaPriceSum = 0;
        for (Pizza pizza : orderedPizzas) {
            pizzaPriceSum += pizza.priceInPence();
        }

        // Checks if pizza price + delivery is the same as the total price.
        if (pizzaPriceSum + 100 != orderToValidate.getPriceTotalInPence()) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        // ---------- Checks if restaurant is open and if ordered from one restaurant ----------

        // Holds the restaurant that the pizzas are ordered from.
        Set<Restaurant> pizzaOriginRestaurant = new HashSet<>();

        for (Restaurant restaurant : definedRestaurants) {
            for (Pizza pizza : orderedPizzas) {
                // Checks if pizza is on the menu of the restaurant.
                if (asList(restaurant.menu()).contains(pizza)) {
                    // Checks if restaurant is open at time of order.
                    if (!asList(restaurant.openingDays()).contains(LocalDate.now().getDayOfWeek())) {
                        orderToValidate.setOrderStatus(OrderStatus.INVALID);
                        orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                        return orderToValidate;
                    }
                }

                //  Checks that all pizzas are from the same restaurant.
                // Converted menu to an arrayList so can use the contains method.
                if (asList(restaurant.menu()).contains(pizza)) {
                    pizzaOriginRestaurant.add(restaurant);
                    // If the set ever goes above size 1 then pizzas are from more than 1 restaurant.
                    if (pizzaOriginRestaurant.size() > 1) {
                        orderToValidate.setOrderStatus(OrderStatus.INVALID);
                        orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                        return orderToValidate;
                    }
                }
            }
        }

        // -------------------- CREDIT CARD CHECKS ---------------------
        CreditCardInformation creditCardInfo = orderToValidate.getCreditCardInformation();
        // ---------- Checks if card number is valid ----------
        char[] creditCardNo = creditCardInfo.getCreditCardNumber().toCharArray();
        boolean validCCNumber = true;

        // Loop looks for any characters in the card number.
        for (char digit : creditCardNo) {   // Converts string to char array for a foreach loop
            if (!Character.isDigit(digit)) {
                validCCNumber = false;
            }
        }

        // Checks credit card length.
        if (creditCardNo.length != 16) {
            validCCNumber = false;
        }

        if (!validCCNumber) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        // ---------- Checks if cvv valid ----------
        String cvv = creditCardInfo.getCvv();

        // Loop looks for any characters in the cvv.
        boolean validCVV = true;
        for (char digit : cvv.toCharArray()) {
            if (!Character.isDigit(digit)) {
                validCVV = false;
            }
        }

        if (cvv.length() != 3) {
            validCVV = false;
        }

        if (!validCVV) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        // ---------- Checks if card is not expired ----------
        String[] expiryDateString = creditCardInfo.getCreditCardExpiry().split("/");
        LocalDate expiryDate;
        int expiryMonth = Integer.parseInt(expiryDateString[0]);
        int expiryYear = Integer.parseInt(expiryDateString[1]) + 2000;
        // Converts integers into LocalDate of the last day of expiry month.

        try {
            expiryDate = YearMonth.of(expiryYear, expiryMonth).atEndOfMonth();
        } catch (DateTimeException e) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        // Checks if card expiry date is before the current date (expired).
        if (expiryDate.isBefore(LocalDate.now())) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        // ---------- Sets order to valid as all check have been passed -----------
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }
}
