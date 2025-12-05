package modelsTests;

import models.Delivery;
import models.utils.Status;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryTests {

    @Test
    void createsDelivery_whenStatusProvided() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        Delivery d = assertDoesNotThrow(() ->
                new Delivery(start, start.plusHours(2), 25, Status.ENROUTE)
        );
        assertNotNull(d);
    }

    @Test
    void throws_whenStatusNull() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        assertThrows(ValidationException.class,
                () -> new Delivery(start, start.plusHours(2), 25, null));
    }
}
