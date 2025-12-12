package modelsTests;

import models.Certification;
import models.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CertificationTests {

    private static class ConcreteEmployee extends Employee {
        public ConcreteEmployee() {
            super("Test", "User", "test@example.com", "12345678901", null);
        }
    }

    private Employee employee;

    @BeforeEach
    void resetRegistry() {
        Certification.certifications.clear();
        employee = new ConcreteEmployee();
    }

    @Test
    void createsCertification_whenDataIsValid() {
        LocalDateTime now = LocalDateTime.now();

        Certification c = assertDoesNotThrow(() ->
                new Certification(employee, "CERT-001", "Cash Handling", now)
        );

        assertNotNull(c);
        assertEquals(1, Certification.certifications.size());
        assertSame(c, Certification.certifications.get(0));
        assertTrue(employee.getCertifications().contains(c));
        assertSame(employee, c.getEmployee());
    }

    @Test
    void throws_whenEmployeeIsNull() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification(null, "CERT-001", "Cash Handling", now)
        );

        assertTrue(ex.getMessage().contains("Employee"));
        assertEquals(0, Certification.certifications.size());
    }

    @Test
    void throws_whenCertificationIdIsBlank() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification(employee, "   ", "Cash Handling", now)
        );

        assertTrue(
                ex.getMessage().toLowerCase().contains("required")
                        || ex.getMessage().toLowerCase().contains("blank")
                        || ex.getMessage().toLowerCase().contains("invalid")
        );
        assertEquals(0, Certification.certifications.size());
        assertEquals(0, employee.getCertifications().size());
    }

    @Test
    void throws_whenCertificationNameIsBlank() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification(employee, "CERT-002", "   ", now)
        );
        assertTrue(
                ex.getMessage().toLowerCase().contains("required")
                        || ex.getMessage().toLowerCase().contains("blank")
                        || ex.getMessage().toLowerCase().contains("invalid")
        );
        assertEquals(0, Certification.certifications.size());
    }

    @Test
    void throws_whenTimeOfCompletionIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification(employee, "CERT-003", "POS Operation", null)
        );
        assertTrue(
                ex.getMessage().toLowerCase().contains("required")
                        || ex.getMessage().toLowerCase().contains("null")
        );
        assertEquals(0, Certification.certifications.size());
    }

    @Test
    void throws_whenCertificationIdDuplicates_caseInsensitive_trimmed() {
        LocalDateTime now = LocalDateTime.now();

        Certification first = new Certification(employee, "Cert-XYZ", "Refunds", now);
        assertEquals(1, Certification.certifications.size());

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification(employee, "  cert-xyz  ", "Refunds - Advanced", now.plusDays(1))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("unique"));

        assertEquals(1, Certification.certifications.size());
        assertSame(first, Certification.certifications.getFirst());
    }

    @Test
    void registryNotUpdatedOnFailedCreation() {
        LocalDateTime now = LocalDateTime.now();

        assertThrows(ValidationException.class, () ->
                new Certification(employee, "   ", "Some Name", now)
        );
        assertEquals(0, Certification.certifications.size());

        Certification ok = new Certification(employee, "OK-1", "Valid Name", now);
        assertEquals(1, Certification.certifications.size());
        assertSame(ok, Certification.certifications.get(0));
    }
}