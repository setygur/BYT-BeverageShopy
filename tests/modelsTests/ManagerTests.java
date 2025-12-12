package modelsTests;

import models.Employee;
import models.Manager;
import models.Person;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerTests {

    private static class TestEmployee extends Employee {
        public TestEmployee(String name, String surname, String email, String pesel, String passport) {
            super(name, surname, email, pesel, passport);
        }
    }

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(Person.class, Manager.class);
    }

    private Manager manager(String suffix, double score, double bonusPct) {
        return new Manager(
                "M" + suffix, "Boss", "m" + suffix + "@corp",
                "99010112345", null,
                score, bonusPct
        );
    }

    @Test
    void ctor_addsToStaticList_whenValid() {
        int before = Manager.managers.size();

        Manager m = assertDoesNotThrow(() -> manager("01", 4.0, 10.0));

        assertNotNull(m);
        assertEquals(before + 1, Manager.managers.size());
        assertTrue(Manager.managers.contains(m));
    }

    @Test
    void ctor_throws_whenManagerEvaluationScoreNegative_dueToRangeMin0() {
        assertThrows(ValidationException.class, () ->
                manager("02", -0.01, 10.0)
        );
    }

    @Test
    void ctor_throws_whenEmailNotUnique_dueToPersonUnique() {
        assertDoesNotThrow(() ->
                new Manager("A", "B", "dup@corp", "99010112345", null, 1.0, 5.0)
        );

        assertThrows(ValidationException.class, () ->
                new Manager("C", "D", "dup@corp", "88010112345", null, 2.0, 5.0)
        );
    }

    @Test
    void getSalary_calculatesFromMockedBaseAndBonusPercent() {
        Manager m = manager("03", 3.0, 10.0);
        assertEquals(1200.0 + (1200.0 * 0.10), m.getSalary(), 1e-9);
    }

    @Test
    void addManaged_throws_whenNull() {
        Manager m = manager("04", 3.0, 0.0);
        assertThrows(ValidationException.class, () -> m.addManaged(null));
    }

    @Test
    void removeManaged_throws_whenNull() {
        Manager m = manager("05", 3.0, 0.0);
        assertThrows(ValidationException.class, () -> m.removeManaged(null));
    }

    @Test
    void setManaged_throws_whenAnyArgumentNull() {
        Manager m = manager("06", 3.0, 0.0);
        Employee e1 = new TestEmployee("E1", "S", "e1@corp", "99010112345", null);
        Employee e2 = new TestEmployee("E2", "S", "e2@corp", "99010112346", null);

        assertThrows(ValidationException.class, () -> m.setManaged(null, e2));
        assertThrows(ValidationException.class, () -> m.setManaged(e1, null));
    }

    @Test
    void addManaged_currentlyCausesStackOverflow_dueToMutualRecursion_ManagerAddManaged_EmployeeSetManager() {
        Manager m = manager("07", 3.0, 0.0);
        Employee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);

        assertThrows(StackOverflowError.class, () -> m.addManaged(e));
    }

    @Test
    void removeManaged_currentlyCausesStackOverflow_ifEmployeeManagerIsSet_dueToMutualRecursion() {
        Manager m = manager("08", 3.0, 0.0);
        Employee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);
        setEmployeeManagerDirect(e, m);
        addManagedDirect(m, e);

        assertThrows(StackOverflowError.class, () -> m.removeManaged(e));
    }

    @Test
    void addTrainee_throws_whenNotManagedByThisManager() {
        Manager m = manager("09", 3.0, 0.0);
        Employee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);

        ValidationException ex = assertThrows(ValidationException.class, () -> m.addTrainee(e));
        assertTrue(ex.getMessage().toLowerCase().contains("does not manage"));
    }

    @Test
    void addTrainee_currentlyCausesStackOverflow_whenEmployeeIsManaged_dueToEmployeeAddManagerCall() {
        Manager m = manager("10", 3.0, 0.0);
        Employee e = new TestEmployee("E", "S", "e@corp", "99010112345", null);

        addManagedDirect(m, e);

        assertThrows(StackOverflowError.class, () -> m.addTrainee(e));
    }

    @Test
    void setTrainee_throws_whenNewTraineeNotManaged() {
        Manager m = manager("11", 3.0, 0.0);
        Employee oldT = new TestEmployee("Old", "S", "old@corp", "99010112345", null);
        Employee newT = new TestEmployee("New", "S", "new@corp", "99010112346", null);

        addTrainedDirect(m, oldT);

        ValidationException ex = assertThrows(ValidationException.class, () -> m.setTrainee(oldT, newT));
        assertTrue(ex.getMessage().toLowerCase().contains("does not manage"));
    }

    private void setEmployeeManagerDirect(Employee e, Manager m) {
        try {
            var f = Employee.class.getDeclaredField("manager");
            f.setAccessible(true);
            f.set(e, m);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void addManagedDirect(Manager m, Employee e) {
        List<Employee> list = TestUtils.getField(m, "managed", List.class);
        if (!list.contains(e)) list.add(e);
    }

    @SuppressWarnings("unchecked")
    private void addTrainedDirect(Manager m, Employee e) {
        List<Employee> list = TestUtils.getField(m, "trained", List.class);
        if (!list.contains(e)) list.add(e);
    }
}
