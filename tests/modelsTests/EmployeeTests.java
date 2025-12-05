package modelsTests;


import models.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTests {

    private static class TestEmployee extends Employee {

        public TestEmployee(String name, String surname, String email,
                            String peselNumber, String passportNumber) {
            super(name, surname, email, peselNumber, passportNumber);
        }
    }

    @Test
    void ctor_allowsPeselOnly() {
        assertDoesNotThrow(() ->
                new TestEmployee(
                        "John", "Silverhand", "john@corp",
                        "99010112345", null
                )
        );
    }

    @Test
    void ctor_allowsPassportOnly() {
        assertDoesNotThrow(() ->
                new TestEmployee(
                        "Jane", "Doe", "jane@corp",
                        null, "AB1234567"
                )
        );
    }

    @Test
    void ctor_allowsBothPeselAndPassport() {
        assertDoesNotThrow(() ->
                new TestEmployee(
                        "V", "Merc", "v@nc",
                        "80010199999", "PL123456"
                )
        );
    }

    @Test
    void ctor_throwsWhenBothPeselAndPassportNull() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new TestEmployee(
                        "Adam", "Smasher", "adam@arasaka",
                        null, null
                )
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(
                msg.contains("either") ||
                        msg.contains("pesel") ||
                        msg.contains("passport"),
                "Expected message mentioning either PESEL or passport, but was: " + ex.getMessage()
        );
    }

    @Test
    void toString_containsPersonAndEmployeeFields() {
        TestEmployee emp = new TestEmployee(
                "Johnny", "Silverhand", "johnny@samurai",
                "75010112345", "JP999999"
        );

        String s = emp.toString();
        assertTrue(s.contains("Johnny"), "toString should contain name");
        assertTrue(s.contains("Silverhand"), "toString should contain surname");
        assertTrue(s.contains("johnny@samurai"), "toString should contain email");
        assertTrue(s.contains("75010112345"), "toString should contain peselNumber");
        assertTrue(s.contains("JP999999"), "toString should contain passportNumber");
    }

    @Test
    void setBaseSalary_setsStaticField() throws Exception {
        Employee.setBaseSalary(3500.75);
        Field baseSalaryField = Employee.class.getDeclaredField("baseSalary");
        baseSalaryField.setAccessible(true);
        double value = (double) baseSalaryField.get(null);

        assertEquals(3500.75, value, 1e-9);
    }

    @Test
    void setBaseSalary_allowsNegative_currentImplementation() throws Exception {
        Employee.setBaseSalary(-100.0);

        Field baseSalaryField = Employee.class.getDeclaredField("baseSalary");
        baseSalaryField.setAccessible(true);
        double value = (double) baseSalaryField.get(null);

        assertEquals(-100.0, value, 1e-9,
                "Currently setBaseSalary does not enforce @Range(min=0); " +
                        "if you add validation later, update this test.");
    }
}
