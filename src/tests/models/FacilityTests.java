package tests.models;

import models.Facility;
import models.utils.Address;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class FacilityTests {

    @Test
    void createsFacility_whenAddressProvided() {
        Facility f = assertDoesNotThrow(() ->
                new Facility(new Address("Night City", "Afterlife Ave", "42B", 12345))
        );
        assertNotNull(f);
    }

    @Test
    void throws_whenAddressMissing() {
        assertThrows(ValidationException.class, () -> new Facility(null));
    }
}
