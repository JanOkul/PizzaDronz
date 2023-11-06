package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static java.util.Arrays.asList;

public class OrderValidation implements uk.ac.ed.inf.ilp.interfaces.OrderValidation {
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        // -------------------- PIZZA CHECKS --------------------
        Pizza[] ordered_pizzas = orderToValidate.getPizzasInOrder();
        // ---------- Checks if too many pizzas have been sent ----------
        if (ordered_pizzas.length > 4) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        // ---------- Checks if no pizzas have been ordered ----------
        if (ordered_pizzas.length < 1) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            return orderToValidate;
        }

        // ---------- Checks for undefined pizzas -----------
        for (Pizza pizza: ordered_pizzas) {
            if (pizza.name().equals("UNDEFINED")) {
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                return orderToValidate;
            }
        }

        // ---------- Checks if pizza price is accurate ----------
        int pizza_price_sum = 0;
        for (Pizza pizza: ordered_pizzas) {
            pizza_price_sum += pizza.priceInPence();
        }

        // Checks if pizza price + delivery is the same as the total price.
        if (pizza_price_sum != (orderToValidate.getPriceTotalInPence()-100)) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        // ---------- Checks if restaurant is open and if ordered from one restaurant ----------

        // Holds the restaurant that the pizzas are ordered from.
        // Set used to eliminate duplicates of the same restaurant
        Set<Restaurant> restaurant_count = new HashSet<Restaurant>();
        // Loops through each restaurant.
        for (Restaurant current_restaurant: definedRestaurants) {
            // Loops through each pizza in the ordered pizza.
            for (Pizza current_pizza: ordered_pizzas) {
                // If pizza is in the current restaurants menu, check if restaurant is closed.
                // Converted menu to an arrayList so can use the contains method.
                if (asList(current_restaurant.menu()).contains(current_pizza)) {

                    // Checks if restaurant is open.
                    // Converted menu to an arrayList so can use the contains method.
                    if (!asList(current_restaurant.openingDays()).contains(LocalDate.now().getDayOfWeek())) {
                        orderToValidate.setOrderStatus(OrderStatus.INVALID);
                        orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                        return orderToValidate;
                    }
                }

                //  Checks that all pizzas are from the same restaurant.
                // Converted menu to an arrayList so can use the contains method.
                if (asList(current_restaurant.menu()).contains(current_pizza)) {
                    restaurant_count.add(current_restaurant);
                    // If the set ever goes above size 1 then pizzas are from more than 1 restaurant.
                    if (restaurant_count.size() > 1) {
                        orderToValidate.setOrderStatus(OrderStatus.INVALID);
                        orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                        return orderToValidate;
                    }
                }
            }
        }

        // -------------------- CREDIT CARD CHECKS ---------------------
        CreditCardInformation credit_card_information = orderToValidate.getCreditCardInformation();
        // ---------- Checks if card number is valid ----------
        String credit_card_number = credit_card_information.getCreditCardNumber();
        boolean valid_cc_number = true;

        // Loop looks for any characters in the card number.
        for (char card_digit: credit_card_number.toCharArray()) {   // Converts string to char array for a foreach loop
            // Checks if there is a character in card number.
            if (!Character.isDigit(card_digit)) {
                valid_cc_number = false;
            }
        }

        // Checks credit card length.
        if (credit_card_number.length() != 16) {valid_cc_number = false;}

        // Sets order as invalid if credit card number is invalid.
        if (!valid_cc_number) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        // ---------- Checks if cvv valid ----------
        String cvv = credit_card_information.getCvv();
        boolean valid_cvv = true;
        for (char cvv_char: cvv.toCharArray()) {
            if (!Character.isDigit(cvv_char)) {
                valid_cvv = false;
            }
        }

        if (cvv.length() != 3) {valid_cvv = false;}

        if (!valid_cvv) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        // ---------- Checks if card is not expired ----------
        String[] expiry_date_string = credit_card_information.getCreditCardExpiry().split("/");

        int month_of_expiry = Integer.parseInt(expiry_date_string[0]);
        int year_of_expiry = Integer.parseInt(expiry_date_string[1])+2000;
        // Converts integers into LocalDate of the last day of expiry month.

        LocalDate expiry_date = YearMonth.of(year_of_expiry, month_of_expiry).atEndOfMonth();

        LocalDate current_date = LocalDate.now();

        // Checks if card expiry date is before the current date (expired).
        if (expiry_date.isBefore(current_date)) {
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
