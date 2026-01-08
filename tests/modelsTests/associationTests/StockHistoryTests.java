package modelsTests.associationTests;

import models.*;
import models.utils.Address;
import models.utils.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StockHistoryTests {

    @Test
    void recordDelivery_addsToHistory() {
        // Create facility → auto creates stock
        Facility f = new Facility(new Address("City", "Street", "Building", 11111));
        Stock s = f.getStock();

        assertNotNull(s);
        assertEquals(0, s.getDeliveryHistory().size());

        // Create a delivery (from a warehouse for now)
        Warehouse w = new Warehouse(f, 1000, false);

        Delivery d = new Delivery(
                LocalDateTime.now().minusHours(1),
                null,
                200,
                Status.ENROUTE,
                w
        );

        // Record delivery in stock history
        LocalDateTime before = s.getLastUpdated();
        s.recordDelivery(d);

        assertEquals(1, s.getDeliveryHistory().size());
        assertTrue(s.getDeliveryHistory().contains(d));

        // Check timestamp updated
        assertNotEquals(before, s.getLastUpdated());
    }


    @Test
    void historyMultipleDeliveries() {
        Facility f = new Facility(new Address("City2", "Street2", "B2", 22222));
        Stock s = f.getStock();

        Warehouse w = new Warehouse(f, 2000, true);

        Delivery d1 = new Delivery(LocalDateTime.now().minusHours(3), null, 150, Status.ENROUTE, w);
        Delivery d2 = new Delivery(LocalDateTime.now().minusHours(2), null, 100, Status.PENDING, w);

        s.recordDelivery(d1);
        s.recordDelivery(d2);

        assertEquals(2, s.getDeliveryHistory().size());
        assertTrue(s.getDeliveryHistory().contains(d1));
        assertTrue(s.getDeliveryHistory().contains(d2));
    }
}
