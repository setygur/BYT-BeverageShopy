package modelsTests;

import models.Certification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CertificationTests {
    @BeforeEach
    void resetRegistry() {
        Certification.certifications.clear();
    }

    @Test
    void createsCertification_whenDataIsValid() {
        LocalDateTime now = LocalDateTime.now();

        Certification c = assertDoesNotThrow(() ->
                new Certification("CERT-001", "Cash Handling", now)
        );

        assertNotNull(c);
        assertEquals(1, Certification.certifications.size(), "Should register exactly one certification");
        assertSame(c, Certification.certifications.get(0), "Created instance should be in the registry");
    }

    @Test
    void throws_whenCertificationIdIsBlank() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification("   ", "Cash Handling", now)
        );

        assertTrue(
                ex.getMessage().toLowerCase().contains("required")
                        || ex.getMessage().toLowerCase().contains("blank")
                        || ex.getMessage().toLowerCase().contains("invalid"),
                "Expected a not-blank/required message but was: " + ex.getMessage()
        );
        assertEquals(0, Certification.certifications.size(), "Invalid creation must not be registered");
    }

    @Test
    void throws_whenCertificationNameIsBlank() {
        LocalDateTime now = LocalDateTime.now();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification("CERT-002", "   ", now)
        );
        assertTrue(
                ex.getMessage().toLowerCase().contains("required")
                        || ex.getMessage().toLowerCase().contains("blank")
                        || ex.getMessage().toLowerCase().contains("invalid"),
                "Expected a not-blank/required message but was: " + ex.getMessage()
        );
        assertEquals(0, Certification.certifications.size());
    }

    @Test
    void throws_whenTimeOfCompletionIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification("CERT-003", "POS Operation", null)
        );
        assertTrue(
                ex.getMessage().toLowerCase().contains("required")
                        || ex.getMessage().toLowerCase().contains("null"),
                "Expected a not-null/required message but was: " + ex.getMessage()
        );
        assertEquals(0, Certification.certifications.size());
    }

    @Test
    void throws_whenCertificationIdDuplicates_caseInsensitive_trimmed() {
        LocalDateTime now = LocalDateTime.now();

        Certification first = new Certification("Cert-XYZ", "Refunds", now);
        assertEquals(1, Certification.certifications.size());

        ValidationException ex = assertThrows(ValidationException.class, () ->
                new Certification("  cert-xyz  ", "Refunds - Advanced", now.plusDays(1))
        );
        assertTrue(
                ex.getMessage().toLowerCase().contains("unique"),
                "Expected uniqueness message but was: " + ex.getMessage()
        );

        assertEquals(1, Certification.certifications.size());
        assertSame(first, Certification.certifications.getFirst());
    }

    @Test
    void registryNotUpdatedOnFailedCreation() {
        LocalDateTime now = LocalDateTime.now();

        assertThrows(ValidationException.class, () ->
                new Certification("   ", "Some Name", now)
        );
        assertEquals(0, Certification.certifications.size());

        Certification ok = new Certification("OK-1", "Valid Name", now);
        assertEquals(1, Certification.certifications.size());
        assertSame(ok, Certification.certifications.get(0));
    }
}
