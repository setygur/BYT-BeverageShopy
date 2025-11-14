package tests.modelsTests;


import models.Warehouse;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTests {

    @Test
    void throws_whenCapacityNegative() {
        assertThrows(ValidationException.class, () -> new Warehouse(-5, true));
    }
}
