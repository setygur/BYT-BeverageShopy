package modelsTests;

import models.*;
import models.utils.Address;
import models.utils.OrderQualifier;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import models.Facility;

public class ShopTests {

    @BeforeEach
    void reset() {
        // Updated: Reset Employee list instead of Cashier list
        TestUtils.resetObjectLists(Shop.class, Order.class, Employee.class, OrderQualifier.class);
    }

    // Helper to create an Employee acting as a Cashier
    private Employee createCashier(String suffix) {
        Employee e = new Employee(
                "John", "Doe", "john" + suffix + "@corp",
                "99010112345", null
        );
        e.becomeCashier(true, "CASH-" + suffix, 4.0);
        return e;
    }

    @Test
    void ctor_addsToStaticList_whenValid() {
        int before = Shop.shops.size();
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());

        assertNotNull(s);
        assertEquals(before + 1, Shop.shops.size());
        assertTrue(Shop.shops.contains(s));
    }

    @Test
    void ctor_throws_whenDateOfLastStockNull() {
        assertThrows(ValidationException.class, () -> new Shop(
            new Facility(new Address("City", "Street", "Building", 11111)),
            null
        ));
    }

    @Test
    void ctor_throws_whenDateOfLastStockInFuture() {
        assertThrows(ValidationException.class, () -> new Shop(
            new Facility(new Address("City", "Street", "Building", 11111)),
            LocalDateTime.now().plusDays(1)
        ));
    }

    @Test
    void getDaysFromLastStock_returnsZero_whenToday() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        assertEquals(0, s.getDaysFromLastStock());
    }

    @Test
    void getDaysFromLastStock_returnsPositive_forPastDate() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now().minusDays(3));
        int days = s.getDaysFromLastStock();
        assertTrue(days >= 3, "Expected at least 3 days; got " + days);
    }

    @Test
    void addOrder_throws_whenAnyArgumentNull() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        Employee c = createCashier("01");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () -> s.addOrder(null, c, o));
        assertThrows(ValidationException.class, () -> s.addOrder(LocalDateTime.now(), null, o));
        assertThrows(ValidationException.class, () -> s.addOrder(LocalDateTime.now(), c, null));
    }

    @Test
    void addOrder_throws_whenActorIsNotCashier() {
        // New Test: Ensure we can't pass a regular Employee (or Manager/Loader) as a Cashier
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        Employee notCashier = new Employee("Not", "C", "nc@c", "99010112345", null);
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        // Should throw because EmployeeType is NONE (or unrelated)
        assertThrows(ValidationException.class, () ->
                s.addOrder(LocalDateTime.now(), notCashier, o)
        );
    }

    @Test
    void removeOrder_throws_whenAnyArgumentNull() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        Employee c = createCashier("02");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () -> s.removeOrder(null, c, o));
        assertThrows(ValidationException.class, () -> s.removeOrder(LocalDateTime.now(), null, o));
        assertThrows(ValidationException.class, () -> s.removeOrder(LocalDateTime.now(), c, null));
    }

    @Test
    void setOrder_throws_whenAnyArgumentNull() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        Employee c = createCashier("03");

        // Assuming OrderQualifier constructor now accepts (LocalDateTime, Employee)
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
    void addOrder_handlesRecursion_correctly() {
        // Previously: "currentlyCausesStackOverflow"
        // Now: Should pass without error if guards are implemented correctly
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        Employee c = createCashier("52");
        LocalDateTime t = LocalDateTime.now();
        Order o = new Order(1L, t, 0.0);

        assertDoesNotThrow(() -> s.addOrder(t, c, o));

        // Verify association exists
        OrderQualifier key = new OrderQualifier(t, c);
        assertSame(o, s.getOrder(t, c));
    }

    @Test
    void getOrder_returnsMappedOrder_whenInsertedDirectlyIntoOrdersMap() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Shop s = new Shop(f, LocalDateTime.now());
        Employee c = createCashier("05");
        LocalDateTime t = LocalDateTime.now();
        Order o = new Order(1L, t, 0.0);

        OrderQualifier key = new OrderQualifier(t, c);

        @SuppressWarnings("unchecked")
        Map<OrderQualifier, Order> map = TestUtils.getField(s, "orders", Map.class);
        map.put(key, o);

        assertSame(o, s.getOrder(t, c));
    }
}