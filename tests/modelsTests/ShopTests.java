package modelsTests;

import models.*;
import models.utils.OrderQualifier;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShopTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Shop.class, Order.class, Cashier.class, OrderQualifier.class);
    }

    private Cashier cashier(String suffix) {
        return new Cashier(
                "John", "Doe", "john" + suffix + "@corp",
                "99010112345", null,
                true, "CASH-" + suffix, 4.0
        );
    }

    @Test
    void ctor_addsToStaticList_whenValid() {
        int before = Shop.shops.size();
        Shop s = new Shop(LocalDateTime.now());

        assertNotNull(s);
        assertEquals(before + 1, Shop.shops.size());
        assertTrue(Shop.shops.contains(s));
    }

    @Test
    void ctor_throws_whenDateOfLastStockNull() {
        assertThrows(ValidationException.class, () -> new Shop(null));
    }

    @Test
    void ctor_throws_whenDateOfLastStockInFuture() {
        assertThrows(ValidationException.class, () -> new Shop(LocalDateTime.now().plusDays(1)));
    }

    @Test
    void getDaysFromLastStock_returnsZero_whenToday() {
        Shop s = new Shop(LocalDateTime.now());
        assertEquals(0, s.getDaysFromLastStock());
    }

    @Test
    void getDaysFromLastStock_returnsPositive_forPastDate() {
        Shop s = new Shop(LocalDateTime.now().minusDays(3));
        int days = s.getDaysFromLastStock();
        assertTrue(days >= 3, "Expected at least 3 days; got " + days);
    }

    @Test
    void addOrder_throws_whenAnyArgumentNull() {
        Shop s = new Shop(LocalDateTime.now());
        Cashier c = cashier("01");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () -> s.addOrder(null, c, o));
        assertThrows(ValidationException.class, () -> s.addOrder(LocalDateTime.now(), null, o));
        assertThrows(ValidationException.class, () -> s.addOrder(LocalDateTime.now(), c, null));
    }

    @Test
    void removeOrder_throws_whenAnyArgumentNull() {
        Shop s = new Shop(LocalDateTime.now());
        Cashier c = cashier("02");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () -> s.removeOrder(null, c, o));
        assertThrows(ValidationException.class, () -> s.removeOrder(LocalDateTime.now(), null, o));
        assertThrows(ValidationException.class, () -> s.removeOrder(LocalDateTime.now(), c, null));
    }

    @Test
    void setOrder_throws_whenAnyArgumentNull() {
        Shop s = new Shop(LocalDateTime.now());
        Cashier c = cashier("03");

        OrderQualifier oq1 = new OrderQualifier(LocalDateTime.now(), c);
        OrderQualifier oq2 = new OrderQualifier(LocalDateTime.now().plusMinutes(1), c);

        Order o1 = new Order(1L, LocalDateTime.now(), 0.0);
        Order o2 = new Order(2L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () -> s.setOrder(null, o1, oq2, o2));
        assertThrows(ValidationException.class, () -> s.setOrder(oq1, null, oq2, o2));
        assertThrows(ValidationException.class, () -> s.setOrder(oq1, o1, null, o2));
        assertThrows(ValidationException.class, () -> s.setOrder(oq1, o1, oq2, null));
    }

    @Test
    void addOrder_currentlyCausesStackOverflow_dueToMutualRecursion_ShopAddOrder_OrderAddShop() {
        Shop s = new Shop(LocalDateTime.now());
        Cashier c = cashier("04");
        LocalDateTime t = LocalDateTime.now();
        Order o = new Order(1L, t, 0.0);

        assertThrows(StackOverflowError.class, () -> s.addOrder(t, c, o));
    }

    @Test
    void getOrder_returnsMappedOrder_whenInsertedDirectlyIntoOrdersMap() {
        Shop s = new Shop(LocalDateTime.now());
        Cashier c = cashier("05");
        LocalDateTime t = LocalDateTime.now();
        Order o = new Order(1L, t, 0.0);

        OrderQualifier key = new OrderQualifier(t, c);

        @SuppressWarnings("unchecked")
        Map<OrderQualifier, Order> map = TestUtils.getField(s, "orders", Map.class);
        map.put(key, o);

        assertSame(o, s.getOrder(t, c));
    }
}