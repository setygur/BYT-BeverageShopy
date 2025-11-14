package tests.models;

import models.Order;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    @Test
    void throws_whenTimestampNull() {
        assertThrows(ValidationException.class,
                () -> new Order(10L, null, 0));
    }
}
