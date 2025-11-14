package tests.models;

import models.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTests {

    @BeforeEach
    void reset() {
        resetObjectLists(Employee.class);
    }

    @Test
    void createsEmployee_whenPassportProvided() {
        Employee emp = assertDoesNotThrow(() ->
                new Employee("Alex", "Smith", "alex@corp", null, "PASS-001")
        );
        assertNotNull(emp);
        assertEquals(1, Employee.employees.size());
    }

    @Test
    void throws_whenNoIdDocsProvided() {
        assertThrows(ValidationException.class, () ->
                new Employee("Alex", "Smith", "a@corp", null, null)
        );
    }

    private void resetObjectLists(Class<?>... classes) {
        for (Class<?> type : classes) {
            for (Field f : type.getDeclaredFields()) {
                try {
                    if (f.isAnnotationPresent(persistence.ObjectList.class)
                            && List.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        List<?> list = (List<?>) f.get(null);
                        if (list != null) list.clear();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
