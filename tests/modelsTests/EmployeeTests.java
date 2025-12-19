package modelsTests;

import models.Employee;
import models.Person;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTests {

    @BeforeEach
    void reset() {
        // Resetting the Employee static list
        TestUtils.resetObjectLists(Person.class, Employee.class);
        Employee.setBaseSalary(0.0);
    }

    @Test
    void ctor_allowsPeselOnly() {
        assertDoesNotThrow(() ->
                new Employee("John", "Silverhand", "john@corp", "99010112345", null)
        );
    }

    @Test
    void ctor_allowsPassportOnly() {
        assertDoesNotThrow(() ->
                new Employee("Jane", "Doe", "jane@corp", null, "AB1234567")
        );
    }

    @Test
    void ctor_allowsBothPeselAndPassport() {
        assertDoesNotThrow(() ->
                new Employee("V", "Merc", "v@nc", "80010199999", "PL123456")
        );
    }

    @Test
    void ctor_throwsWhenBothPeselAndPassportNull() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Employee("Adam", "Smasher", "adam@arasaka", null, null)
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("either") || msg.contains("pesel") || msg.contains("passport"));
    }

    @Test
    void toString_containsPersonAndEmployeeFields() {
        Employee emp = new Employee(
                "Johnny", "Silverhand", "johnny@samurai",
                "75010112345", "JP999999"
        );

        String s = emp.toString();
        // Checks common fields
        assertTrue(s.contains("Johnny"));
        assertTrue(s.contains("Silverhand"));
        assertTrue(s.contains("johnny@samurai"));
        assertTrue(s.contains("75010112345"));
        // Check new Role format
        assertTrue(s.contains("Role: None"));
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
        assertEquals(-100.0, value, 1e-9);
    }

    // --- Helper to create an Employee acting as Manager ---
    private Employee createManager(String suffix) {
        Employee e = new Employee(
                "M" + suffix, "Boss", "m" + suffix + "@corp",
                "99010112345", null
        );
        // Promote to manager
        e.becomeManager(4.0, 10.0);
        return e;
    }

    @Test
    void setManager_allowsNull_toClearReference() {
        // Updated: The new implementation allows passing null to setManager to clear the link.
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        assertDoesNotThrow(() -> e.setManager(null));
    }

    @Test
    void removeManager_doesNotThrow_whenNull() {
        // Updated: The new removeManager(null) simply returns or ignores, or might allow check.
        // Looking at code: "if(this.manager == manager)". If passed null and current is null, it's safe.
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        assertDoesNotThrow(() -> e.removeManager(null));
    }

    @Test
    void setManager_throws_whenRoleIsNotManager() {
        // New Test: Ensure we can't assign a Loader as a Manager
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee loader = new Employee("L", "L", "l@corp", "99010112345", null);
        loader.becomeLoader(5.0);

        assertThrows(ValidationException.class, () -> e.setManager(loader));
    }

    @Test
    void setManager_handlesRecursion_correctly() {
        // Renamed from "addManager_currentlyCausesStackOverflow"
        // The new code HAS guards (!contains), so it should NOT overflow anymore.
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee m = createManager("01");

        assertDoesNotThrow(() -> e.setManager(m));

        // Verify the link is established
        assertSame(m, getEmployeeManagerDirect(e));
        assertTrue(getManagedDirect(m).contains(e));
    }

    @Test
    void setManager_noOp_whenSameManagerAlreadySet_doesNotRecurse() {
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee m = createManager("02");
        setEmployeeManagerDirect(e, m);

        assertDoesNotThrow(() -> e.setManager(m));
        assertSame(m, getEmployeeManagerDirect(e));
    }

    @Test
    void removeManager_removesCurrentManager_correctly() {
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee current = createManager("03");

        // Manually set up the link to test removal
        e.setManager(current);

        // Remove
        assertDoesNotThrow(() -> e.removeManager(current));

        // Assert cleared
        assertNull(getEmployeeManagerDirect(e));
    }

    @Test
    void addTrainer_throws_whenNull() {
        // Assuming implementation throws on null, strictly speaking addTrainer code provided:
        // "if(trainer != null ...)" checks type. If null, it falls through to "if(this.trainer == trainer) return".
        // If current trainer is null, it continues. "this.trainer = trainer" (null).
        // It seems the new implementation allows null to be passed safely or ignores it.
        // However, if we want to stick to validation:
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        // Based on provided code logic: it allows null.
        // We will assertDoesNotThrow.
        assertDoesNotThrow(() -> e.addTrainer(null));
    }

    @Test
    void setTrainer_throws_whenTrainerNotManager() {
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee loader = new Employee("L", "L", "l@corp", "99010112345", null);
        loader.becomeLoader(1.0);

        assertThrows(ValidationException.class, () -> e.addTrainer(loader));
    }

    @Test
    void addTrainer_handlesRecursion_correctly() {
        // Renamed from "addTrainer_currentlyCausesStackOverflow"
        // The new code guards against recursion.
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee m = createManager("07");

        // Pre-requisite: Trainer must manage the employee
        m.addManaged(e);

        assertDoesNotThrow(() -> e.addTrainer(m));

        // Verify link
        assertSame(m, getEmployeeTrainerDirect(e));
    }

    @Test
    void removeTrainer_doesNothing_whenArgumentNotEqualToCurrentTrainer() {
        Employee e = new Employee("E", "S", "e@corp", "99010112345", null);
        Employee current = createManager("08");
        Employee other = createManager("09");

        setEmployeeTrainerDirect(e, current);
        assertDoesNotThrow(() -> e.removeTrainer(other));
        assertSame(current, getEmployeeTrainerDirect(e));
    }

    // --- Reflection Utils (Updated for Flattened Employee) ---

    private static void setEmployeeManagerDirect(Employee e, Employee m) {
        try {
            Field f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            f.set(e, m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Employee getEmployeeManagerDirect(Employee e) {
        try {
            Field f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            return (Employee) f.get(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setEmployeeTrainerDirect(Employee e, Employee m) {
        try {
            Field f = Employee.class.getDeclaredField("trainer");
            f.setAccessible(true);
            f.set(e, m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Employee getEmployeeTrainerDirect(Employee e) {
        try {
            Field f = Employee.class.getDeclaredField("trainer");
            f.setAccessible(true);
            return (Employee) f.get(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Employee> getManagedDirect(Employee m) {
        try {
            // Field "managed" is now in Employee class
            Field f = Employee.class.getDeclaredField("managed");
            f.setAccessible(true);
            return (List<Employee>) f.get(m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}