package modelsTests;


import models.Warehouse;
import models.Facility;
import models.utils.Address;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTests {

    @Test
    void throws_whenCapacityNegative() {
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        assertThrows(ValidationException.class, () -> new Warehouse(f, -5, true));
    }
}
