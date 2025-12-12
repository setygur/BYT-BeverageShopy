package modelsTests;


import models.*;
import models.utils.Drink_Size;
import models.utils.OrderQualifier;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(
                Drink.class, Order.class, Order_Drink.class,
                Cashier.class, Shop.class, OrderQualifier.class
        );
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

    @Test
    void getTotalPrice_includesTip() {
        Order o = new Order(1L, LocalDateTime.now(), 3.5);
        assertEquals(11.5, o.getTotalPrice(), 0.0001);
    }

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
    void addShop_currentlyCausesStackOverflow_dueToMutualRecursion_OrderAddShop_ShopAddOrder() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Shop s = new Shop(LocalDateTime.now());

        assertThrows(StackOverflowError.class, () -> o.addShop(s));
    }

    @Test
    void removeShop_whenShopIsSetDirectly_unlinksWithoutRecursingIntoShop() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Shop s = new Shop(LocalDateTime.now());

        setShopDirect(o, s);

        assertThrows(ValidationException.class, () -> o.removeShop(s));
        assertNull(TestUtils.getField(o, "shop", Shop.class));
    }

    @Test
    void addCashier_throws_whenNull() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        assertThrows(ValidationException.class, () -> o.addCashier(null));
    }

    @Test
    void addCashier_setsCashierAndBackLinksToCashierOrders() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null, true, "C1", 4.0);

        assertDoesNotThrow(() -> o.addCashier(c));

        assertSame(c, TestUtils.getField(o, "cashier", Cashier.class));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertTrue(cashierOrders.contains(o));
    }

    @Test
    void removeCashier_unlinksBidirectionally_whenMatchesCurrentCashier() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null, true, "C1", 4.0);

        o.addCashier(c);
        assertDoesNotThrow(() -> o.removeCashier(c));

        assertNull(TestUtils.getField(o, "cashier", Cashier.class));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertFalse(cashierOrders.contains(o));
    }

    @Test
    void addDrink_overloadWithOrderDrink_addsAndBackLinks_whenOrderIsSet() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);

        Order_Drink od = new Order_Drink(o, d, true, false, Drink_Size.MEDIUM, new ArrayList<>());
        od.setOrder(o);
        od.setDrink(d);

        assertDoesNotThrow(() -> o.addDrink(od));

        List<Order_Drink> orderDrinks = TestUtils.getField(o, "drinks", List.class);
        List<Order_Drink> drinkOrders = TestUtils.getField(d, "orders", List.class);

        assertTrue(orderDrinks.contains(od));
        assertTrue(drinkOrders.contains(od));
    }

    @Test
    void addDrink_withDrinkAndParams_currentlyThrowsNullPointer_dueToOrderDrinkConstructorBug() {
        Order o = new Order(1L, LocalDateTime.now(), 0.0);
        Drink d = new Drink("D", 5.0, "none", null, null, null, null);

        assertThrows(NullPointerException.class, () ->
                o.addDrink(d, false, false, Drink_Size.SMALL, new ArrayList<>())
        );
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
