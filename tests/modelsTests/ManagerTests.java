package modelsTests;

import models.Employee;
import models.Person;
import models.utils.EmployeeType;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Person.class, Employee.class);
    }

    // Helper to create an Employee acting as a Manager
    private Employee createManager(String suffix, double score, double bonusPct) {
        Employee e = new Employee(
                "M" + suffix, "Boss", "m" + suffix + "@corp",
                "99010112345", null
        );
        e.becomeManager(score, bonusPct);
        return e;
    }

    @Test
    void becomeManager_setsTypeAndFields_correctly() {
        Employee e = new Employee("M", "B", "m@c", "99010112345", null);

        assertDoesNotThrow(() -> e.becomeManager(4.0, 10.0));

        assertEquals(EmployeeType.MANAGER, e.getType());
        assertTrue(Employee.employees.contains(e));
    }

    @Test
    void becomeManager_throws_whenManagerEvaluationScoreNegative() {
        Employee e = new Employee("M", "B", "m@c", "99010112345", null);

        assertThrows(ValidationException.class, () ->
                e.becomeManager(-0.01, 10.0)
        );
    }

    @Test
    void becomeManager_throws_whenBonusNegative() {
        Employee e = new Employee("M", "B", "m@c", "99010112345", null);

        assertThrows(ValidationException.class, () ->
                e.becomeManager(4.0, -5.0)
        );
    }
    

    @Test
    void getSalary_calculatesFromMockedBaseAndBonusPercent() {
        Employee m = createManager("03", 3.0, 10.0);

        // Base 1200 + 10% bonus
        assertEquals(1200.0 + (1200.0 * 0.10), m.getSalary(), 1e-9);
    }

    @Test
    void addManaged_throws_whenNull() {
        Employee m = createManager("04", 3.0, 0.0);
        assertThrows(ValidationException.class, () -> m.addManaged(null));
    }

    @Test
    void removeManaged_doesNotThrow_whenNull() {
        // Implementation typically ignores nulls in remove
        Employee m = createManager("05", 3.0, 0.0);
        assertDoesNotThrow(() -> m.removeManaged(null));
    }

    @Test
    void addManaged_handlesRecursion_correctly() {
        // Previously: "currentlyCausesStackOverflow"
        // Now: Should succeed because we added guard clauses (!contains)
        Employee m = createManager("07", 3.0, 0.0);
        Employee e = new Employee("E", "S", "e@corp", "88010112345", null);

        assertDoesNotThrow(() -> m.addManaged(e));

        // Verify relationship
        assertTrue(getManagedDirect(m).contains(e));
        assertEquals(m, getEmployeeManagerDirect(e));
    }

    @Test
    void removeManaged_handlesRecursion_correctly() {
        // Previously: "currentlyCausesStackOverflow"
        Employee m = createManager("08", 3.0, 0.0);
        Employee e = new Employee("E", "S", "e@corp", "88010112345", null);

        // Setup relationship manually to test removal
        setEmployeeManagerDirect(e, m);
        addManagedDirect(m, e);

        assertDoesNotThrow(() -> m.removeManaged(e));

        // Verify removal
        assertFalse(getManagedDirect(m).contains(e));
        assertNull(getEmployeeManagerDirect(e));
    }

    @Test
    void addTrainee_throws_whenNotManagedByThisManager() {
        Employee m = createManager("09", 3.0, 0.0);
        Employee e = new Employee("E", "S", "e@corp", "88010112345", null);

        // Subordinate relationship doesn't exist yet
        ValidationException ex = assertThrows(ValidationException.class, () -> m.addTrainee(e));
        assertTrue(ex.getMessage().toLowerCase().contains("does not manage"));
    }

    @Test
    void addTrainee_handlesRecursion_correctly() {
        // Previously: "currentlyCausesStackOverflow"
        Employee m = createManager("10", 3.0, 0.0);
        Employee e = new Employee("E", "S", "e@corp", "88010112345", null);

        // Must be managed first
        addManagedDirect(m, e);

        assertDoesNotThrow(() -> m.addTrainee(e));

        // Verify
        assertTrue(getTrainedDirect(m).contains(e));
    }

    @Test
    void addManaged_throws_whenActorIsNotManager() {
        Employee notManager = new Employee("Not", "M", "nm@c", "99010112345", null);
        Employee e = new Employee("E", "S", "e@corp", "88010112345", null);

        assertThrows(ValidationException.class, () -> notManager.addManaged(e));
    }

    // --- Reflection Utils (Updated for generic Employee class) ---

    private void setEmployeeManagerDirect(Employee e, Employee m) {
        try {
            var f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            f.set(e, m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Employee getEmployeeManagerDirect(Employee e) {
        try {
            var f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            return (Employee) f.get(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void addManagedDirect(Employee m, Employee e) {
        List<Employee> list = TestUtils.getField(m, "managed", List.class);
        if (!list.contains(e)) list.add(e);
    }

    @SuppressWarnings("unchecked")
    private List<Employee> getManagedDirect(Employee m) {
        return TestUtils.getField(m, "managed", List.class);
    }

    @SuppressWarnings("unchecked")
    private void addTrainedDirect(Employee m, Employee e) {
        List<Employee> list = TestUtils.getField(m, "trained", List.class);
        if (!list.contains(e)) list.add(e);
    }

    @SuppressWarnings("unchecked")
    private List<Employee> getTrainedDirect(Employee m) {
        return TestUtils.getField(m, "trained", List.class);
    }
}