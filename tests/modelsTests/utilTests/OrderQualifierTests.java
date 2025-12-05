package modelsTests.utilTests;

import models.Cashier;
import models.utils.OrderQualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderQualifierTests {
    @BeforeEach
    void resetStatics() {
        OrderQualifier.orderQualifiers.clear();
        Cashier.cashiers.clear();
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
        assertTrue(
                "Expected message related to null/required/invalid, but was: " + ex.getMessage(),
                msg.contains("invalid") || msg.contains("null") || msg.contains("required")
        );
        assertEquals(0, OrderQualifier.orderQualifiers.size());
    }

    @Test
    void ctor_throwsWhenCashierIsNull() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new OrderQualifier(now, null)
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(
                "Expected message related to null/required/invalid, but was: " + ex.getMessage(),
                msg.contains("invalid") || msg.contains("null") || msg.contains("required")
        );
        assertEquals(0, OrderQualifier.orderQualifiers.size());
    }

    @Test
    void equals_returnsTrueForSameInstance() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier = createValidCashier("003");

        OrderQualifier q = new OrderQualifier(now, cashier);

        assertEquals(q, q);
    }

    @Test
    void equals_returnsTrueForSameTimeAndSameCashierInstance() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier = createValidCashier("004");

        OrderQualifier q1 = new OrderQualifier(now, cashier);
        OrderQualifier q2 = new OrderQualifier(now, cashier);

        assertEquals("Two qualifiers with same time and same Cashier instance should be equal",
                q1, q2);
    }

    @Test
    void equals_returnsFalseForDifferentCashier() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier1 = createValidCashier("005");
        Cashier cashier2 = createValidCashier("006"); // different instance & id

        OrderQualifier q1 = new OrderQualifier(now, cashier1);
        OrderQualifier q2 = new OrderQualifier(now, cashier2);

        assertNotEquals("Qualifiers with same time but different Cashier instances should not be equal",
                q1, q2);
    }

    @Test
    void equals_returnsFalseForDifferentTime() {
        Cashier cashier = createValidCashier("007");
        LocalDateTime t1 = LocalDateTime.now();
        LocalDateTime t2 = t1.plusSeconds(5);

        OrderQualifier q1 = new OrderQualifier(t1, cashier);
        OrderQualifier q2 = new OrderQualifier(t2, cashier);

        assertNotEquals("Qualifiers with different times but same Cashier should not be equal",
                q1, q2);
    }

    @Test
    void equals_handlesNullAndDifferentType() {
        LocalDateTime now = LocalDateTime.now();
        Cashier cashier = createValidCashier("008");

        OrderQualifier q = new OrderQualifier(now, cashier);

        assertNotEquals("OrderQualifier should not be equal to null", q, null);
        assertNotEquals("OrderQualifier should not be equal to other types", q, "some string");
    }
}
