package modelsTests;


import models.*;
import models.utils.Drink_Size;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DrinkTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Drink.class, Order.class, Order_Drink.class,
                Coffee.class, Tea.class, Milk.class, Fruit.class);
    }

    @Test
    void constructor_addsToStaticList_whenValid() {
        int before = Drink.drinks.size();

        Drink d = new Drink("Sunrise", 10.0, "nuts", null, null, null, null);

        assertNotNull(d);
        assertEquals(before + 1, Drink.drinks.size());
        assertTrue(Drink.drinks.contains(d));
    }

    @Test
    void throws_whenNameBlank() {
        assertThrows(ValidationException.class, () ->
                new Drink("   ", 10.0, "none", null, null, null, null)
        );
    }

    @Test
    void throws_whenAllergensBlank() {
        assertThrows(ValidationException.class, () ->
                new Drink("Latte", 10.0, "   ", null, null, null, null)
        );
    }

    @Test
    void throws_whenPriceNegative() {
        assertThrows(ValidationException.class, () ->
                new Drink("Sunrise", -1.0, "nuts",
                        new Coffee(5),
                        new Tea(models.utils.TypeOfTea.GREEN),
                        new Milk(models.utils.TypeOfMilk.OAT),
                        new Fruit(Collections.singletonList("Apple"), true)
                )
        );
    }

    @Test
    void addOrder_throws_whenOrderNull() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        assertThrows(ValidationException.class, () ->
                d.addOrder(null, false, false, Drink_Size.SMALL, new ArrayList<>())
        );
    }

    @Test
    void addOrder_throws_whenSizeNull() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, java.time.LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () ->
                d.addOrder(o, false, false, null, new ArrayList<>())
        );
    }

    @Test
    void addOrder_throws_whenToppingsNull() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, java.time.LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () ->
                d.addOrder(o, false, false, Drink_Size.SMALL, null)
        );
    }

    @Test
    void addOrder_OrderDrinkOverload_addsAndBackLinks_whenOrderIsSet() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, java.time.LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(o, d, true, false, Drink_Size.MEDIUM, new ArrayList<>());
        od.setOrder(o);
        od.setDrink(d);

        assertDoesNotThrow(() -> d.addOrder(od));

        List<Order_Drink> drinkOrders = TestUtils.getField(d, "orders", List.class);
        List<Order_Drink> orderDrinks = TestUtils.getField(o, "drinks", List.class);

        assertTrue(drinkOrders.contains(od));
        assertTrue(orderDrinks.contains(od));
    }

    @Test
    void addOrder_OrderAndParamsVersion_currentlyThrowsNullPointer_dueToOrderDrinkConstructorBug() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, java.time.LocalDateTime.now(), 0.0);

        assertThrows(NullPointerException.class, () ->
                d.addOrder(o, false, false, Drink_Size.SMALL, new ArrayList<>())
        );
    }

    @Test
    void removeOrder_OrderDrinkOverload_removesAndBackUnlinks_whenPresent() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, java.time.LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(o, d, true, false, Drink_Size.BIG, new ArrayList<>());
        od.setOrder(o);
        od.setDrink(d);

        d.addOrder(od);

        assertDoesNotThrow(() -> d.removeOrder(od));

        List<Order_Drink> drinkOrders = TestUtils.getField(d, "orders", List.class);
        List<Order_Drink> orderDrinks = TestUtils.getField(o, "drinks", List.class);

        assertFalse(drinkOrders.contains(od));
        assertFalse(orderDrinks.contains(od));
    }

    @Test
    void setOrder_replacesAssociation_whenOldExists() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, java.time.LocalDateTime.now(), 0.0);

        Order_Drink oldOd = new Order_Drink(o, d, true, false, Drink_Size.SMALL, new ArrayList<>());
        oldOd.setOrder(o);
        oldOd.setDrink(d);

        Order_Drink newOd = new Order_Drink(o, d, false, true, Drink_Size.XXL, new ArrayList<>());
        newOd.setOrder(o);
        newOd.setDrink(d);

        d.addOrder(oldOd);
        assertDoesNotThrow(() -> d.setOrder(oldOd, newOd));

        List<Order_Drink> drinkOrders = TestUtils.getField(d, "orders", List.class);
        assertFalse(drinkOrders.contains(oldOd));
        assertTrue(drinkOrders.contains(newOd));
    }
}
