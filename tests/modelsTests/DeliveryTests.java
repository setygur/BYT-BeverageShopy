package modelsTests;

import models.Delivery;
import models.Warehouse;
import models.utils.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryTests {

    @Test
    void deliveryConnectsToWarehouse_whenWarehouseProvided() {
        Warehouse w1 = new Warehouse(5000, false);

        LocalDateTime start = LocalDateTime.now().minusHours(2);

        Delivery d1 = new Delivery(
                start,
                null,
                200,
                Status.ENROUTE,
                w1
        );

        assertEquals(1, w1.getDeliveries().size());
        assertEquals(w1, d1.getSourceWarehouse());
        assertTrue(w1.getDeliveries().contains(d1));
    }

    @Test
    void deliveryHasNoWarehouse_whenNullProvided() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);

        Delivery d2 = new Delivery(
                start,
                null,
                100,
                Status.ENROUTE,
                null
        );

        assertNull(d2.getSourceWarehouse());
    }
}
