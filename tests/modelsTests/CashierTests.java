package modelsTests;
import models.Order;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CashierTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Cashier.class, Order.class);
    }

    @Test
    void createsCashier_whenDataIsValid() {
        Cashier c = assertDoesNotThrow(() ->
                new Cashier(
                        "John", "Silverhand", "john@corp",
                        "99010112345", null,
                        true, "CASH-001", 4.7
                )
        );
        assertNotNull(c);
        assertTrue(Cashier.cashiers.contains(c));
    }

    @Test
    void throws_whenCashierIdIsBlank() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Cashier(
                        "John", "Silverhand", "john@corp",
                        "99010112345", null,
                        true, "   ", 3.2
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("required")
                || ex.getMessage().toLowerCase().contains("blank")
                || ex.getMessage().toLowerCase().contains("invalid"));
    }

    @Test
    void throws_whenCashierIdIsDuplicate() {
        assertDoesNotThrow(() ->
                new Cashier(
                        "Alice", "W.", "alice@corp",
                        "99010112345", null,
                        true, "CASH-XYZ", 3.9
                )
        );

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Cashier(
                        "Bob", "Q.", "bob@corp",
                        "88010112345", null,
                        true, "CASH-XYZ", 4.1
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("unique"));
    }

    @Test
    void throws_whenBothPeselAndPassportMissing() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Cashier(
                        "Eve", "Parker", "eve@corp",
                        null, null,
                        true, "CASH-777", 4.2
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("either")
                || ex.getMessage().toLowerCase().contains("pesel")
                || ex.getMessage().toLowerCase().contains("passport"));
    }

    @Test
    void addOrder_addsToCashierAndBackLinksToOrder() {
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null,
                true, "C1", 4.0);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertDoesNotThrow(() -> c.addOrder(o));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertTrue(cashierOrders.contains(o));

        Cashier orderCashier = TestUtils.getField(o, "cashier", Cashier.class);
        assertSame(c, orderCashier);
    }

    @Test
    void addOrder_isIdempotent_whenSameOrderAddedTwice() {
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null,
                true, "C1", 4.0);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        c.addOrder(o);
        c.addOrder(o);

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertEquals(1, cashierOrders.size());
    }

    @Test
    void removeOrder_removesFromCashierAndBackUnlinksFromOrder() {
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null,
                true, "C1", 4.0);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        c.addOrder(o);
        assertDoesNotThrow(() -> c.removeOrder(o));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertFalse(cashierOrders.contains(o));

        Cashier orderCashier = TestUtils.getField(o, "cashier", Cashier.class);
        assertNull(orderCashier);
    }

    @Test
    void setOrder_replacesOldOrderWithNewOrder_andMaintainsBackLinks() {
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null,
                true, "C1", 4.0);
        Order oldO = new Order(1L, LocalDateTime.now(), 0.0);
        Order newO = new Order(2L, LocalDateTime.now(), 0.0);

        c.addOrder(oldO);

        assertDoesNotThrow(() -> c.setOrder(oldO, newO));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertFalse(cashierOrders.contains(oldO));
        assertTrue(cashierOrders.contains(newO));

        assertNull(TestUtils.getField(oldO, "cashier", Cashier.class));
        assertSame(c, TestUtils.getField(newO, "cashier", Cashier.class));
    }

    @Test
    void getSalary_returnsHoursTimesRatePlusTips_mockedValues() {
        Cashier c = new Cashier("A", "B", "a@corp", "99010112345", null,
                true, "C1", 4.0);
        assertEquals(40.0 * 20.0 + 150.0, c.getSalary(), 1e-9);
    }
}
