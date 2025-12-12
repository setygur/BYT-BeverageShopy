package modelsTests.associationTests;

import models.Facility;
import models.Stock;
import models.utils.Address;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FacilityStockTests {

    @Test
    void facilityHasExactlyOneStock() {
        Address address = new Address("City", "Street", "Building", 12345);
        Facility f = new Facility(address);

        // 1..1 composition
        Stock s = f.getStock();
        assertNotNull(s);
        assertEquals(f, s.getFacility());

        // List integrity
        assertTrue(Facility.facilities.contains(f));
        assertTrue(Stock.stocks.contains(s));
    }
}
