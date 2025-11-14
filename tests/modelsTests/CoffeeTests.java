package modelsTests;

import models.Coffee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class CoffeeTests {
    @BeforeEach
    void resetRegistry() {
        Coffee.coffees.clear();
    }

    @Test
    void createsCoffee_whenLevelWithinRange() {
        Coffee c = assertDoesNotThrow(() -> new Coffee(5));
        assertNotNull(c);
        assertEquals(1, Coffee.coffees.size(), "Valid coffee should be registered");
        assertSame(c, Coffee.coffees.getFirst());
    }

    @Test
    void acceptsBoundary_minInclusive() {
        Coffee c = assertDoesNotThrow(() -> new Coffee(1));
        assertNotNull(c);
        assertEquals(1, Coffee.coffees.size());
    }

    @Test
    void acceptsBoundary_maxInclusive() {
        Coffee c = assertDoesNotThrow(() -> new Coffee(10));
        assertNotNull(c);
        assertEquals(1, Coffee.coffees.size());
    }

    @Test
    void throws_whenBelowMin() {
        ValidationException ex = assertThrows(ValidationException.class, () -> new Coffee(0));
        assertTrue(
                ex.getMessage().toLowerCase().contains("range")
                        || ex.getMessage().toLowerCase().contains("min")
                        || ex.getMessage().toLowerCase().contains("invalid"),
                "Expected a range/min validation message but was: " + ex.getMessage()
        );
        assertEquals(0, Coffee.coffees.size(), "Invalid instance must not be registered");
    }

    @Test
    void throws_whenAboveMax() {
        ValidationException ex = assertThrows(ValidationException.class, () -> new Coffee(11));
        assertTrue(
                ex.getMessage().toLowerCase().contains("range")
                        || ex.getMessage().toLowerCase().contains("max")
                        || ex.getMessage().toLowerCase().contains("invalid"),
                "Expected a range/max validation message but was: " + ex.getMessage()
        );
        assertEquals(0, Coffee.coffees.size());
    }

    @Test
    void registry_accumulatesOnlyValidInstances() {
        assertThrows(ValidationException.class, () -> new Coffee(0));
        assertEquals(0, Coffee.coffees.size());

        Coffee c1 = new Coffee(3);
        assertEquals(1, Coffee.coffees.size());
        Coffee c2 = new Coffee(7);
        assertEquals(2, Coffee.coffees.size());
        assertSame(c1, Coffee.coffees.get(0));
        assertSame(c2, Coffee.coffees.get(1));
    }
}
