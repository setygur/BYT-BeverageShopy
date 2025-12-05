package modelsTests.associationTests;

import models.Drink;
import models.Order_Drink;
import models.utils.Drink_Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderOrdersTests {
    private static class Order {
        private List<Order_Drink> drinks = new java.util.ArrayList<>();

        public void addDrinkToOrder(Drink drink, boolean heated, boolean cooled, Drink_Size size) {
            drinks.add(new Order_Drink(drink, heated, cooled, size));
        }

        public void removeDrinkFromOrder(Drink drink, boolean heated, boolean cooled, Drink_Size size) {
            for (Order_Drink o : drinks) {
                if (Order_Drink.order_Drinks.equals(new Order_Drink(drink, heated, cooled, size))) {
                    drinks.remove(o);
                    return;
                }
            }
        }
    }

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        Order_Drink.order_Drinks.clear();
    }

    private Drink createDrink(double basePrice) {
        return new Drink("Test drink", basePrice, "None",
                null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    private List<Order_Drink> getDrinksList(Order order) {
        try {
            Field f = Order.class.getDeclaredField("drinks");
            f.setAccessible(true);
            return (List<Order_Drink>) f.get(order);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read 'drinks' field", e);
        }
    }

    @Test
    void addDrinkToOrder_addsNewOrderDrink() {
        Drink drink = createDrink(10.0);

        assertDoesNotThrow(() ->
                order.addDrinkToOrder(drink, true, false, Drink_Size.MEDIUM)
        );

        List<Order_Drink> drinksList = getDrinksList(order);
        assertEquals(1, drinksList.size(), "One Order_Drink should be added to the internal list");

        Order_Drink od = drinksList.get(0);
        assertNotNull(od, "Stored Order_Drink must not be null");

        // Also, static registry is updated by the Order_Drink constructor
        assertEquals(1, Order_Drink.order_Drinks.size(),
                "Order_Drink constructor adds instance to the static order_Drinks list");
    }

    @Test
    void addDrinkToOrder_allowsMultipleEntries() {
        Drink d1 = createDrink(8.0);
        Drink d2 = createDrink(12.0);

        order.addDrinkToOrder(d1, true, false,  Drink_Size.SMALL);
        order.addDrinkToOrder(d2, false, true,  Drink_Size.BIG);

        List<Order_Drink> drinksList = getDrinksList(order);
        assertEquals(2, drinksList.size(), "Two Order_Drink entries should exist in the list");
    }

    @Test
    void addDrinkToOrder_throwsWhenSizeNull_andDoesNotAdd() {
        Drink drink = createDrink(10.0);

        assertThrows(ValidationException.class, () ->
                order.addDrinkToOrder(drink, true, false, null)
        );

        List<Order_Drink> drinksList = getDrinksList(order);
        assertEquals(0, drinksList.size(), "Invalid Order_Drink must not be added to the list");
    }

    //TODO fix
    @Test
    void removeDrinkFromOrder_shouldRemoveMatchingDrink_intendedBehavior() {
        Drink drink = createDrink(10.0);

        order.addDrinkToOrder(drink, true,  false, Drink_Size.MEDIUM);
        order.addDrinkToOrder(drink, false, true,  Drink_Size.MEDIUM);

        List<Order_Drink> drinksListBefore = getDrinksList(order);
        assertEquals(2, drinksListBefore.size(), "Precondition: two drinks in the list");

        order.removeDrinkFromOrder(drink, true, false, Drink_Size.MEDIUM);

        List<Order_Drink> drinksListAfter = getDrinksList(order);

        assertEquals(1, drinksListAfter.size(),
                "After removal, exactly one drink should remain (intended behavior)");

        Order_Drink remaining = drinksListAfter.get(0);

        Order_Drink expected = new Order_Drink(drink, false, true, Drink_Size.MEDIUM);
        assertEquals(expected, remaining,
                "The remaining drink should be the one with heated=false, cooled=true, MEDIUM");
    }

    @Test
    void removeDrinkFromOrder_doesNothingWhenNoMatch_intendedBehavior() {
        Drink drink = createDrink(10.0);

        // Add a SMALL drink
        order.addDrinkToOrder(drink, true, false, Drink_Size.SMALL);

        List<Order_Drink> drinksListBefore = getDrinksList(order);
        assertEquals(1, drinksListBefore.size(), "Precondition: one drink in the list");

        // Try to remove a BIG one (no match intended)
        order.removeDrinkFromOrder(drink, true, false, Drink_Size.BIG);

        List<Order_Drink> drinksListAfter = getDrinksList(order);
        assertEquals(1, drinksListAfter.size(),
                "If no drink matches the requested parameters, list size should stay the same");
    }
}
