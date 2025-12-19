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

public class DrinkTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(
                Drink.class, Order.class, Order_Drink.class,
                Coffee.class, Tea.class, Milk.class, Fruit.class
        );
    }

    // ---------- constructor ----------

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
                new Drink(
                        "Sunrise",
                        -1.0,
                        "nuts",
                        new Coffee(5),
                        new Tea(models.utils.TypeOfTea.GREEN),
                        new Milk(models.utils.TypeOfMilk.OAT),
                        new Fruit(List.of("Apple"), true)
                )
        );
    }

    // ---------- addOrder (association class) ----------

    @Test
    void addOrder_throws_whenOrderNull() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);

        assertThrows(ValidationException.class, () ->
                d.addOrder(
                        null,
                        new HotDrink(),
                        Set.of(),
                        Drink_Size.SMALL,
                        new ArrayList<>()
                )
        );
    }

    @Test
    void addOrder_throws_whenSizeNull() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () ->
                d.addOrder(
                        o,
                        new HotDrink(),
                        Set.of(),
                        null,
                        new ArrayList<>()
                )
        );
    }

    @Test
    void addOrder_throws_whenToppingsNull() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () ->
                d.addOrder(
                        o,
                        new HotDrink(),
                        Set.of(),
                        Drink_Size.SMALL,
                        null
                )
        );
    }

    @Test
    void addOrder_createsOrderDrink_andBackLinks() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        d.addOrder(
                o,
                new ColdDrink(),
                Set.of(new HoneySweetened()),
                Drink_Size.MEDIUM,
                List.of("x")
        );

        List<Order_Drink> drinkOrders =
                TestUtils.getField(d, "orders", List.class);
        List<Order_Drink> orderDrinks =
                TestUtils.getField(o, "drinks", List.class);

        assertEquals(1, drinkOrders.size());
        assertSame(drinkOrders.get(0), orderDrinks.get(0));
    }

    // ---------- removeOrder ----------

    @Test
    void removeOrder_OrderDrinkOverload_removesAndBackUnlinks() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(
                o,
                d,
                new HotDrink(),
                Set.of(),
                Drink_Size.BIG,
                new ArrayList<>()
        );

        d.addOrder(od);
        d.removeOrder(od);

        List<Order_Drink> drinkOrders =
                TestUtils.getField(d, "orders", List.class);
        List<Order_Drink> orderDrinks =
                TestUtils.getField(o, "drinks", List.class);

        assertFalse(drinkOrders.contains(od));
        assertFalse(orderDrinks.contains(od));
    }

    // ---------- setOrder ----------

    @Test
    void setOrder_replacesAssociation_whenOldExists() {
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink oldOd = new Order_Drink(
                o,
                d,
                new HotDrink(),
                Set.of(),
                Drink_Size.SMALL,
                new ArrayList<>()
        );

        Order_Drink newOd = new Order_Drink(
                o,
                d,
                new ColdDrink(),
                Set.of(new SugarSweetened()),
                Drink_Size.XXL,
                new ArrayList<>()
        );

        d.addOrder(oldOd);
        d.setOrder(oldOd, newOd);

        List<Order_Drink> drinkOrders =
                TestUtils.getField(d, "orders", List.class);

        assertFalse(drinkOrders.contains(oldOd));
        assertTrue(drinkOrders.contains(newOd));
    }
}