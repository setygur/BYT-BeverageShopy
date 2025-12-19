package modelsTests;

import models.*;
import models.aspects.*;
import models.utils.Drink_Size;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.*;

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

        assertThrows(NullPointerException.class, () ->
                new Order_Drink(
                        order,
                        drink,
                        new HotDrink(),
                        Set.of(),
                        null,
                        new ArrayList<>()
                )
        );
    }

    @Test
    void throws_whenToppingsNull() {
        Drink drink = new Drink("TestDrink", 15.0, "None", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(NullPointerException.class, () ->
                new Order_Drink(
                        order,
                        drink,
                        new HotDrink(),
                        Set.of(),
                        Drink_Size.SMALL,
                        null
                )
        );
    }

    @Test
    void createsOrderDrink_whenValid() {
        Drink drink = new Drink("TestDrink", 15.0, "None", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = assertDoesNotThrow(() ->
                new Order_Drink(
                        order,
                        drink,
                        new ColdDrink(),
                        Set.of(new HoneySweetened()),
                        Drink_Size.MEDIUM,
                        new ArrayList<>()
                )
        );

        assertNotNull(od);
        assertTrue(Order_Drink.order_Drinks.contains(od));
    }

    @Test
    void additionalCost_smallNoToppingsNoSweeteners() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(
                order,
                drink,
                new HotDrink(),
                Set.of(),
                Drink_Size.SMALL,
                new ArrayList<>()
        );

        assertEquals(10.0, od.getAdditionalCost(), 0.0001);
    }

    @Test
    void additionalCost_xxlWith3ToppingsAnd2Sweeteners() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        ArrayList<String> toppings = new ArrayList<>(List.of("a", "b", "c"));

        Order_Drink od = new Order_Drink(
                order,
                drink,
                new HotDrink(),
                Set.of(new HoneySweetened(), new SugarSweetened()),
                Drink_Size.XXL,
                toppings
        );

        // base 10 + size 6 + toppings 3 + sweeteners 1.0
        assertEquals(20.0, od.getAdditionalCost(), 0.0001);
    }

    @Test
    void equals_trueForSameValues() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink a = new Order_Drink(
                order,
                drink,
                new HotDrink(),
                Set.of(new SugarSweetened()),
                Drink_Size.BIG,
                List.of("x")
        );

        Order_Drink b = new Order_Drink(
                order,
                drink,
                new HotDrink(),
                Set.of(new SugarSweetened()),
                Drink_Size.BIG,
                List.of("x")
        );

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void find_returnsExisting_whenValuesEqual_notByReference() {
        Drink drink = new Drink("D", 10.0, "none", null, null, null, null);
        Order order = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(
                order,
                drink,
                new ColdDrink(),
                Set.of(new HoneySweetened()),
                Drink_Size.MEDIUM,
                List.of("x")
        );

        Order_Drink found = Order_Drink.find(
                order,
                drink,
                new ColdDrink(),
                Set.of(new HoneySweetened()),
                Drink_Size.MEDIUM,
                List.of("x")
        );

        assertSame(od, found);
    }
}