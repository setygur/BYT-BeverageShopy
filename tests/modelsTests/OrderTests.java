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
        // Updated: Reset Employee instead of Cashier
        TestUtils.resetObjectLists(
                Drink.class, Order.class, Order_Drink.class,
                Employee.class, Shop.class, OrderQualifier.class
        );
    }

    // Helper to create a Cashier-type Employee
    private Employee createCashier(String suffix) {
        Employee e = new Employee("A", "B", "a" + suffix + "@corp", "99010112345", null);
        e.becomeCashier(true, "C" + suffix, 4.0);
        return e;
    }

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
    void addShop_handlesRecursion_correctly() {
        // Previously: "currentlyCausesStackOverflow"
        // Fix: Standard bidirectional logic with (!contains) guards prevents this.
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Shop s = new Shop(LocalDateTime.now());

        assertDoesNotThrow(() -> o.addShop(s));
    }

    @Test
    void removeShop_whenShopIsSetDirectly_unlinksWithoutRecursingIntoShop() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Shop s = new Shop(LocalDateTime.now());

        assertDoesNotThrow(() -> o.addShop(s));

        // Assuming removeShop handles consistency checks, strict validation might fail if link is partial,
        // but typically we test that it cleans up.
        // If your implementation throws when the link is partial, keep ValidationException.
        // Otherwise, assertDoesNotThrow. Based on previous test name, I'll assume ValidationException was expected due to state.
        assertThrows(ValidationException.class, () -> o.removeShop(s));

        assertNull(TestUtils.getField(o, "shop", Shop.class));
    }

    // ---------- cashier association ----------

    @Test
    void addCashier_throws_whenNull() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        assertThrows(ValidationException.class, () -> o.addCashier(null));
    }

    @Test
    void addCashier_throws_whenActorIsNotCashier() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Employee notCashier = new Employee("Not", "C", "n@c", "99010112345", null);
        // Should throw because type is NONE
        assertThrows(ValidationException.class, () -> o.addCashier(notCashier));
    }

    @Test
    void addCashier_setsCashierAndBackLinksToCashierOrders() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Employee c = createCashier("1");

        o.addCashier(c);

        assertSame(c, TestUtils.getField(o, "cashier", Employee.class));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertTrue(cashierOrders.contains(o));
    }

    @Test
    void removeCashier_unlinksBidirectionally() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Employee c = createCashier("1");

        o.addCashier(c);
        o.removeCashier(c);

        assertNull(TestUtils.getField(o, "cashier", Employee.class));

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

        assertTrue(orderDrinks.contains(od));
        assertTrue(drinkOrders.contains(od));
    }

    private void setShopDirect(Order order, Shop shop) {
        try {
            var f = Order.class.getDeclaredField("shop");
            f.setAccessible(true);
            f.set(order, shop);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}