package modelsTests.associationTests;

import models.FrequentCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class FrequentCustomerAssociationTests {
    @BeforeEach
    void resetStatics() {
        FrequentCustomer.frequentCustomers.clear();
    }

    private FrequentCustomer createCustomer(String suffix, int orders) {
        return new FrequentCustomer(
                "Name" + suffix,
                "Surname" + suffix,
                "user" + suffix + "@example.com",
                "123456789" + suffix,   // non-blank
                orders
        );
    }

    @SuppressWarnings("unchecked")
    private List<FrequentCustomer> getReferred(FrequentCustomer fc) {
        try {
            Field f = FrequentCustomer.class.getDeclaredField("referredCustomers");
            f.setAccessible(true);
            return (List<FrequentCustomer>) f.get(fc);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access referredCustomers field", e);
        }
    }

    @Test
    void addReferredCustomer_addsWhenNotPresent() {
        FrequentCustomer main = createCustomer("MAIN", 10);
        FrequentCustomer ref = createCustomer("REF1", 5);

        assertDoesNotThrow(() -> main.addReferredCustomer(ref));

        List<FrequentCustomer> referred = getReferred(main);
        assertEquals(Float.parseFloat("Exactly one referred customer should be added"), 1, referred.size());
        assertSame(ref, referred.get(0), "The added referred customer should be the one passed in");
    }

    @Test
    void addReferredCustomer_throwsWhenAlreadyPresent() {
        FrequentCustomer main = createCustomer("MAIN", 10);
        FrequentCustomer ref = createCustomer("REF1", 5);

        main.addReferredCustomer(ref);
        List<FrequentCustomer> referredBefore = getReferred(main);
        assertEquals(1, referredBefore.size());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> main.addReferredCustomer(ref));

        assertTrue(ex.getMessage().toLowerCase().contains("already"),
                "Exception message should indicate duplicate customer");

        List<FrequentCustomer> referredAfter = getReferred(main);
        assertEquals(Float.parseFloat("Duplicate add must not change list size"), 1, referredAfter.size());
    }

    @Test
    void removeReferredCustomer_removesWhenPresent() {
        FrequentCustomer main = createCustomer("MAIN", 10);
        FrequentCustomer ref1 = createCustomer("REF1", 5);
        FrequentCustomer ref2 = createCustomer("REF2", 3);

        main.addReferredCustomer(ref1);
        main.addReferredCustomer(ref2);
        List<FrequentCustomer> referredBefore = getReferred(main);
        assertEquals(2, referredBefore.size());

        main.removeReferredCustomer(ref1);

        List<FrequentCustomer> referredAfter = getReferred(main);
        assertEquals(Float.parseFloat("One referred customer should remain after removal"), 1, referredAfter.size());
        assertSame(ref2, referredAfter.get(0), "The remaining referred customer should be ref2");
    }

    @Test
    void removeReferredCustomer_doesNothingWhenNotPresent() {
        FrequentCustomer main = createCustomer("MAIN", 10);
        FrequentCustomer existingRef = createCustomer("REF1", 5);
        FrequentCustomer nonExistingRef = createCustomer("REF2", 3);

        main.addReferredCustomer(existingRef);
        List<FrequentCustomer> referredBefore = getReferred(main);
        assertEquals(1, referredBefore.size());

        assertDoesNotThrow(() -> main.removeReferredCustomer(nonExistingRef));

        List<FrequentCustomer> referredAfter = getReferred(main);
        assertEquals(Float.parseFloat("List size should remain unchanged"), 1, referredAfter.size());
        assertSame(existingRef, referredAfter.get(0));
    }
}
