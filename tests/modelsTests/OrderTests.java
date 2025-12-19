package modelsTests;

import models.*;
import models.aspects.*;
import models.utils.Drink_Size;
import models.utils.OrderQualifier;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(
                Drink.class, Order.class, Order_Drink.class,
                Cashier.class, Shop.class, OrderQualifier.class
        );
    }

    // ---------- constructor / validation ----------

    @Test
    void throws_whenTimestampNull() {
        assertThrows(ValidationException.class,
                () -> new Order(10L, null, 0));
    }

    @Test
    void throws_whenTimestampInFuture() {
        assertThrows(ValidationException.class,
                () -> new Order(10L, LocalDateTime.now().plusDays(1), 0));
    }

    @Test
    void throws_whenOrderIdNotUnique() {
        LocalDateTime t = LocalDateTime.now();
        assertDoesNotThrow(() -> new Order(10L, t, 0));
        assertThrows(ValidationException.class,
                () -> new Order(10L, t.minusMinutes(1), 0));
    }

    // ---------- total price ----------

    @Test
    void getTotalPrice_emptyOrder_isTipOnly() {
        Order o = new Order(1L, LocalDateTime.now(), 3.5);
        assertEquals(3.5, o.getTotalPrice(), 0.0001);
    }

    @Test
    void getTotalPrice_sumsDrinksAndTip() {
        Order o = new Order(1L, LocalDateTime.now(), 2.0);
        Drink d = new Drink("D", 10.0, "none", null, null, null, null);

        o.addDrink(
                d,
                new HotDrink(),
                Set.of(new HoneySweetened()),
                Drink_Size.BIG,
                List.of("a", "b")
        );

        // base 10 + size 4 + toppings 2 + sweetener 0.5 + tip 2
        assertEquals(18.5, o.getTotalPrice(), 0.0001);
    }

    // ---------- shop association ----------

    @Test
    void addShop_throws_whenNull() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        assertThrows(ValidationException.class, () -> o.addShop(null));
    }

    @Test
    void removeShop_throws_whenNull() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        assertThrows(ValidationException.class, () -> o.removeShop(null));
    }

    @Test
    void setShop_throws_whenAnyArgumentNull() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Shop s1 = new Shop(LocalDateTime.now());
        Shop s2 = new Shop(LocalDateTime.now());

        assertThrows(ValidationException.class, () -> o.setShop(null, s2));
        assertThrows(ValidationException.class, () -> o.setShop(s1, null));
    }

    @Test
    void addShop_linksBidirectionally_withoutRecursion() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Shop s = new Shop(LocalDateTime.now());

        assertDoesNotThrow(() -> o.addShop(s));

        assertSame(s, TestUtils.getField(o, "shop", Shop.class));
        List<Order> shopOrders = TestUtils.getField(s, "orders", List.class);
        assertTrue(shopOrders.contains(o));
    }

    // ---------- cashier association ----------

    @Test
    void addCashier_throws_whenNull() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        assertThrows(ValidationException.class, () -> o.addCashier(null));
    }

    @Test
    void addCashier_setsCashierAndBackLinks() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345",
                null, true, "C1", 4.0);

        o.addCashier(c);

        assertSame(c, TestUtils.getField(o, "cashier", Cashier.class));
        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertTrue(cashierOrders.contains(o));
    }

    @Test
    void removeCashier_unlinksBidirectionally() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345",
                null, true, "C1", 4.0);

        o.addCashier(c);
        o.removeCashier(c);

        assertNull(TestUtils.getField(o, "cashier", Cashier.class));
        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertFalse(cashierOrders.contains(o));
    }

    // ---------- drinks ----------

    @Test
    void addDrink_addsAndBackLinks_correctly() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);

        o.addDrink(
                d,
                new ColdDrink(),
                Set.of(),
                Drink_Size.MEDIUM,
                List.of()
        );

        List<Order_Drink> orderDrinks = TestUtils.getField(o, "drinks", List.class);
        List<Order_Drink> drinkOrders = TestUtils.getField(d, "orders", List.class);

        assertEquals(1, orderDrinks.size());
        assertTrue(drinkOrders.contains(orderDrinks.get(0)));
    }
}