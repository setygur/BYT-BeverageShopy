package modelsTests;

import models.Employee;
import models.Manager;
import models.Person;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTests {

    private static class TestEmployee extends Employee {
        public TestEmployee(String name, String surname, String email,
                            String peselNumber, String passportNumber) {
            super(name, surname, email, peselNumber, passportNumber);
        }
    }

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Person.class, Manager.class);
        Employee.setBaseSalary(0.0);
    }

    @Test
    void ctor_allowsPeselOnly() {
        assertDoesNotThrow(() ->
                new TestEmployee("John", "Silverhand", "john@corp", "99010112345", null)
        );
    }

    @Test
    void ctor_allowsPassportOnly() {
        assertDoesNotThrow(() ->
                new TestEmployee("Jane", "Doe", "jane@corp", null, "AB1234567")
        );
    }

    @Test
    void ctor_allowsBothPeselAndPassport() {
        assertDoesNotThrow(() ->
                new TestEmployee("V", "Merc", "v@nc", "80010199999", "PL123456")
        );
    }

    @Test
    void ctor_throwsWhenBothPeselAndPassportNull() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new TestEmployee("Adam", "Smasher", "adam@arasaka", null, null)
        );

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("either") || msg.contains("pesel") || msg.contains("passport"));
    }

    @Test
    void toString_containsPersonAndEmployeeFields() {
        TestEmployee emp = new TestEmployee(
                "Johnny", "Silverhand", "johnny@samurai",
                "75010112345", "JP999999"
        );

        String s = emp.toString();
        assertTrue(s.contains("Johnny"));
        assertTrue(s.contains("Silverhand"));
        assertTrue(s.contains("johnny@samurai"));
        assertTrue(s.contains("75010112345"));
        assertTrue(s.contains("JP999999"));
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

    private Manager manager(String suffix) {
        return new Manager(
                "M" + suffix, "Boss", "m" + suffix + "@corp",
                "99010112345", null,
                4.0, 10.0
        );
    }

    @Test
    void addManager_throws_whenNull() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        assertThrows(ValidationException.class, () -> e.addManager(null));
    }

    @Test
    void removeManager_throws_whenNull() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        assertThrows(ValidationException.class, () -> e.removeManager(null));
    }

    @Test
    void setManager_throws_whenNull() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        assertThrows(ValidationException.class, () -> e.setManager(null));
    }

    @Test
    void addManager_currentlyCausesStackOverflow_dueToMutualRecursion_EmployeeAddManager_ManagerAddManaged() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager m = manager("01");

        assertThrows(StackOverflowError.class, () -> e.addManager(m));
    }

    @Test
    void setManager_noOp_whenSameManagerAlreadySet_doesNotRecurse() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager m = manager("02");
        setEmployeeManagerDirect(e, m);

        assertDoesNotThrow(() -> e.setManager(m));
        assertSame(m, getEmployeeManagerDirect(e));
    }

    @Test
    void removeManager_removesCurrentManagerRegardlessOfArgument_currentlyRecursesWhenManagerPresent() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager current = manager("03");
        Manager other = manager("04");

        setEmployeeManagerDirect(e, current);

        assertThrows(StackOverflowError.class, () -> e.removeManager(other));
    }

    @Test
    void addTrainer_throws_whenNull() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        assertThrows(ValidationException.class, () -> e.addTrainer(null));
    }

    @Test
    void removeTrainer_throws_whenNull() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        assertThrows(ValidationException.class, () -> e.removeTrainer(null));
    }

    @Test
    void setTrainer_throws_whenAnyArgumentNull() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager m1 = manager("05");
        Manager m2 = manager("06");

        assertThrows(ValidationException.class, () -> e.setTrainer(null, m2));
        assertThrows(ValidationException.class, () -> e.setTrainer(m1, null));
    }

    @Test
    void addTrainer_currentlyCausesStackOverflow_whenTrainerAddsTraineeAndEmployeeCallsBack() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager m = manager("07");
        addManagedDirect(m, e);

        assertThrows(StackOverflowError.class, () -> e.addTrainer(m));
    }

    @Test
    void removeTrainer_doesNothing_whenArgumentNotEqualToCurrentTrainer() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager current = manager("08");
        Manager other = manager("09");

        setEmployeeTrainerDirect(e, current);
        assertDoesNotThrow(() -> e.removeTrainer(other));
        assertSame(current, getEmployeeTrainerDirect(e));
    }

    @Test
    void setTrainer_noOp_whenNewTrainerEqualsCurrentTrainer_doesNotRecurse() {
        TestEmployee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        Manager current = manager("10");

        setEmployeeTrainerDirect(e, current);
        assertDoesNotThrow(() -> e.setTrainer(current, current));
        assertSame(current, getEmployeeTrainerDirect(e));
    }

    private static void setEmployeeManagerDirect(Employee e, Manager m) {
        try {
            Field f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            f.set(e, m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Manager getEmployeeManagerDirect(Employee e) {
        try {
            Field f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            return (Manager) f.get(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setEmployeeTrainerDirect(Employee e, Manager m) {
        try {
            Field f = Employee.class.getDeclaredField("trainer");
            f.setAccessible(true);
            f.set(e, m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Manager getEmployeeTrainerDirect(Employee e) {
        try {
            Field f = Employee.class.getDeclaredField("trainer");
            f.setAccessible(true);
            return (Manager) f.get(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static void addManagedDirect(Manager m, Employee e) {
        java.util.List<Employee> managed = TestUtils.getField(m, "managed", java.util.List.class);
        if (!managed.contains(e)) managed.add(e);
    }
}
