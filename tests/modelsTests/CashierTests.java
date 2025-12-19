package modelsTests;

import models.Employee;
import models.Order;
import models.Person;
import models.utils.EmployeeType;
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
        // Resetting generic Employee list instead of Cashier list
        TestUtils.resetObjectLists(Person.class, Employee.class, Order.class);
    }

    // Helper to create a valid Cashier
    private Employee createCashier(String suffix, String id) {
        Employee e = new Employee(
                "John", "Silverhand", "john" + suffix + "@corp",
                "99010112345", null
        );
        e.becomeCashier(true, id, 4.7);
        return e;
    }

    @Test
    void becomeCashier_setsTypeAndFields_whenDataIsValid() {
        Employee e = new Employee("John", "Silverhand", "john@corp", "99010112345", null);

        assertDoesNotThrow(() ->
                e.becomeCashier(true, "CASH-001", 4.7)
        );

        assertEquals(EmployeeType.CASHIER, e.getType());
        assertTrue(Employee.employees.contains(e));
    }

    @Test
    void becomeCashier_throws_whenCashierIdIsBlank() {
        Employee e = new Employee("John", "Silverhand", "john@corp", "99010112345", null);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                e.becomeCashier(true, "   ", 3.2)
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("required") || msg.contains("blank") || msg.contains("invalid") || msg.contains("id"));
    }

    @Test
    void ctor_throws_whenBothPeselAndPassportMissing() {
        // Validating base Employee constructor logic
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Employee("Eve", "Parker", "eve@corp", null, null)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("either")
                || ex.getMessage().toLowerCase().contains("pesel")
                || ex.getMessage().toLowerCase().contains("passport"));
    }

    @Test
    void addOrder_addsToCashierAndBackLinksToOrder() {
        Employee c = createCashier("1", "C1");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertDoesNotThrow(() -> c.addOrder(o));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertTrue(cashierOrders.contains(o));

        // Note: Order.cashier field is now type Employee
        Employee orderCashier = TestUtils.getField(o, "cashier", Employee.class);
        assertSame(c, orderCashier);
    }

    @Test
    void addOrder_throws_whenNotCashier() {
        // New Test: Ensure regular employees cannot process orders
        Employee notCashier = new Employee("Not", "C", "nc@corp", "99010112345", null);
        Order o = new Order(2L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () -> notCashier.addOrder(o));
    }

    @Test
    void addOrder_isIdempotent_whenSameOrderAddedTwice() {
        Employee c = createCashier("1", "C1");
        Order o = new Order(255L, LocalDateTime.now(), 0.0);

        c.addOrder(o);
        c.addOrder(o);

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertEquals(1, cashierOrders.size());
    }

    @Test
    void removeOrder_removesFromCashierAndBackUnlinksFromOrder() {
        Employee c = createCashier("1", "C1");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        c.addOrder(o);
        assertDoesNotThrow(() -> c.removeOrder(o));

        List<Order> cashierOrders = TestUtils.getField(c, "orders", List.class);
        assertFalse(cashierOrders.contains(o));

        Employee orderCashier = TestUtils.getField(o, "cashier", Employee.class);
        assertNull(orderCashier);
    }

    @Test
    void getSalary_returnsHoursTimesRatePlusTips_mockedValues() {
        // Setup: HandlesCash = true
        Employee c = createCashier("1", "C1");


        assertEquals(250.0, c.getSalary(), 1e-9);
    }
}