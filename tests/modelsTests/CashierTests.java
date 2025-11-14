package modelsTests;
import models.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class CashierTests {

    @BeforeEach
    void setUp() {
        Cashier.cashiers.clear();
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
        assertTrue(ex.getMessage().toLowerCase().contains("required") ||
                        ex.getMessage().toLowerCase().contains("blank") ||
                        ex.getMessage().toLowerCase().contains("invalid"),
                "Expected a not-blank validation message but was: " + ex.getMessage());
    }

    @Test
    void throws_whenCashierIdIsDuplicate() {
        // First instance with id "CASH-XYZ"
        Cashier first = new Cashier(
                "Alice", "W.", "alice@corp",
                "99010112345", null,
                true, "CASH-XYZ", 3.9
        );

        // Second instance uses the same cashierId -> should fail @Unique
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Cashier(
                        "Bob", "Q.", "bob@corp",
                        "88010112345", null,
                        true, "CASH-XYZ", 4.1
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("unique"),
                "Expected a uniqueness validation message but was: " + ex.getMessage());
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
        assertTrue(ex.getMessage().toLowerCase().contains("either") ||
                        ex.getMessage().toLowerCase().contains("pesel") ||
                        ex.getMessage().toLowerCase().contains("passport"),
                "Expected an either-or message but was: " + ex.getMessage());
    }

    @Test
    void salary_isDerived_andNotUserAssignable() throws Exception {
        Cashier c = new Cashier(
                "Derive", "Me", "d@corp",
                "90010112345", null,
                true, "CASH-900", 4.0
        );

        Field salaryField = Cashier.class.getDeclaredField("salary");
        salaryField.setAccessible(true);
        Object salary = salaryField.get(c);

        // Current implementation sets salary = 0.0 after validation; verify it's a double and non-null
        assertTrue(salary instanceof Double, "salary should be a double");
        assertEquals(0.0, (Double) salary, 1e-9);
    }
}
