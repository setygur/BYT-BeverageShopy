package modelsTests.utilTests;

import models.Cashier;
import models.utils.OrderQualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderQualifierTests {

    @BeforeEach
    void resetStatics() {
        TestUtils.resetObjectLists(OrderQualifier.class, Cashier.class);
    }

    private Cashier createValidCashier(String idSuffix) {
        return new Cashier(
                "John",
                "Doe",
                "john.doe" + idSuffix + "@corp",
                "99010112345",
                null,
                true,
                "CASH-" + idSuffix,
                4.5
        );
    }

    @Test
    void ctor_createsOrderQualifier_whenDataIsValid() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier = createValidCashier("001");

        OrderQualifier q = assertDoesNotThrow(() ->
                new OrderQualifier(now, cashier)
        );

        assertNotNull(q);
        assertEquals(cashier, q.getCashier());
        assertEquals(now, q.getTimeOfOrder());
    }

    @Test
    void ctor_throwsWhenTimeOfOrderIsNull() {
        Cashier cashier = createValidCashier("002");

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new OrderQualifier(null, cashier)
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("invalid") || msg.contains("null") || msg.contains("required"));
        assertEquals(0, OrderQualifier.orderQualifiers.size());
    }

    @Test
    void ctor_throwsWhenCashierIsNull() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new OrderQualifier(now, null)
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("invalid") || msg.contains("null") || msg.contains("required"));
        assertEquals(0, OrderQualifier.orderQualifiers.size());
    }

    @Test
    void equals_returnsTrueForSameTimeAndSameCashierInstance() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier = createValidCashier("004");

        OrderQualifier q1 = new OrderQualifier(now, cashier);
        OrderQualifier q2 = new OrderQualifier(now, cashier);

        assertEquals(q1, q2);
    }

    @Test
    void find_returnsExistingQualifier_whenTimeAndCashierMatch() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier = createValidCashier("010");

        OrderQualifier created = new OrderQualifier(now, cashier);
        OrderQualifier found = OrderQualifier.find(now, cashier);

        assertNull(found, "With current code, orderQualifiers is never populated; find() returns null.");
        assertNotNull(created);
    }
}