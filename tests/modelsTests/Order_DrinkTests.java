package modelsTests;


import models.Drink;
import models.Order;
import models.Order_Drink;
import models.utils.Drink_Size;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Order_DrinkTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Drink.class, Order.class, Order_Drink.class);
    }

    @Test
    void throws_whenSizeNull() {
        Drink drink = new Drink("TestDrink", 15.0, "None", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class,
                () -> new Order_Drink(order, drink, true, false, null, new ArrayList<>())
        );
    }

    @Test
    void throws_whenToppingsNull() {
        Drink drink = new Drink("TestDrink", 15.0, "None", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class,
                () -> new Order_Drink(order, drink, true, false, Drink_Size.SMALL, null)
        );
    }

    @Test
    void createsOrderDrink_whenValid() {
        Drink drink = new Drink("TestDrink", 15.0, "None", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = assertDoesNotThrow(() ->
                new Order_Drink(order, drink, true, false, Drink_Size.MEDIUM, new ArrayList<>())
        );
        assertNotNull(od);
        assertTrue(Order_Drink.order_Drinks.contains(od));
    }

    @Test
    void additionalCost_isComputedCorrectly_smallNoToppings() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(order, drink, false, false, Drink_Size.SMALL, new ArrayList<>());

        double additionalCost = TestUtils.getDoubleField(od, "additionalCost");
        assertEquals(10.0, additionalCost, 0.0001);
    }

    @Test
    void additionalCost_isComputedCorrectly_xxlWith3Toppings() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        ArrayList<String> toppings = new ArrayList<>(List.of("a", "b", "c"));
        Order_Drink od = new Order_Drink(order, drink, false, false, Drink_Size.XXL, toppings);

        double additionalCost = TestUtils.getDoubleField(od, "additionalCost");
        assertEquals(10.0 + 6.0 + 3.0, additionalCost, 0.0001);
    }

    @Test
    void equals_trueForSameValues() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        ArrayList<String> toppings = new ArrayList<>(List.of("x"));
        Order_Drink a = new Order_Drink(order, drink, true, false, Drink_Size.BIG, toppings);
        Order_Drink b = new Order_Drink(order, drink, true, false, Drink_Size.BIG, toppings);

        assertEquals(a, b);
    }

    @Test
    void find_returnsExistingOnlyWhenSameToppingsListInstance_dueToReferenceComparisonBug() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        ArrayList<String> toppings = new ArrayList<>(List.of("x"));
        Order_Drink od = new Order_Drink(order, drink, true, false, Drink_Size.MEDIUM, toppings);

        Order_Drink found = Order_Drink.find(order, drink, true, false, Drink_Size.MEDIUM, toppings);
        assertSame(od, found);

        ArrayList<String> toppingsCopy = new ArrayList<>(List.of("x"));
        Order_Drink notFound = Order_Drink.find(order, drink, true, false, Drink_Size.MEDIUM, toppingsCopy);
        assertNull(notFound);
    }

    @Test
    void settersAndGetters_work() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(order, drink, true, false, Drink_Size.SMALL, new ArrayList<>());
        od.setHeated(false);
        od.setCooled(true);
        od.setSize(Drink_Size.BIG);
        od.setDrink(drink);
        od.setOrder(order);

        ArrayList<String> t = new ArrayList<>(List.of("a"));
        od.setToppings(t);

        assertFalse(od.isHeated());
        assertTrue(od.isCooled());
        assertEquals(Drink_Size.BIG, od.getSize());
        assertSame(drink, od.getDrink());
        assertSame(order, od.getOrder());
        assertSame(t, od.getToppings());
    }
}
