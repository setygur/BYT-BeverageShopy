package modelsTests.associationTests;

import models.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftEmployeeTests {

    private LocalDateTime now() { return LocalDateTime.now(); }

    // concrete subclass for testing
    private static class TestEmployee extends Employee {
        public TestEmployee() {
            super("John", "Doe", "mail@mail.com", "123", null);
        }
    }

    // ----------------------------------------------------
    // 1. CREATION TEST (BIDIRECTIONAL)
    // ----------------------------------------------------
    @Test
    public void testCreateAssociationShiftEmployee() {
        Shift s = new Shift(now(), now().plusHours(2));
        Employee e = new TestEmployee();

        s.addEmployee(e);

        assertTrue(s.getEmployees().contains(e));   // direct
        assertTrue(e.getShifts().contains(s));      // reverse
    }

    // ----------------------------------------------------
    // 2. DELETION TEST (BIDIRECTIONAL)
    // ----------------------------------------------------
    @Test
    public void testDeleteAssociationShiftEmployee() {
        Shift s = new Shift(now(), now().plusHours(2));
        Employee e = new TestEmployee();

        s.addEmployee(e);
        s.removeEmployee(e);

        assertFalse(s.getEmployees().contains(e));
        assertFalse(e.getShifts().contains(s));
    }

    // ----------------------------------------------------
    // 3. MODIFICATION TEST
    // (employee works multiple shifts, shift has multiple employees)
    // ----------------------------------------------------
    @Test
    public void testModifyEmployeeAssociations() {
        Employee e = new TestEmployee();

        Shift s1 = new Shift(now(), now().plusHours(2));
        Shift s2 = new Shift(now().plusHours(3), now().plusHours(5));

        s1.addEmployee(e);
        s2.addEmployee(e);

        assertEquals(2, e.getShifts().size());
        assertTrue(e.getShifts().contains(s1));
        assertTrue(e.getShifts().contains(s2));
    }

}
