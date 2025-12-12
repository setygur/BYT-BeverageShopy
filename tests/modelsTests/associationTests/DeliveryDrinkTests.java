package modelsTests.associationTests;

import models.*;
import models.utils.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryDrinkTests {

    @Test
    void deliveryAndDrinkConnectBothWays() {
        Delivery delivery = new Delivery(
                LocalDateTime.now().minusHours(1),
                null,
                50,
                Status.ENROUTE,
                null
        );

        Drink coffee = new Drink("Mocha", 3.0, "milk", null, null, null, null);
        Drink tea    = new Drink("Tea", 2.0, "none", null, new Tea(TypeOfTea.JASMINE), null, null);

        delivery.addDrink(coffee);
        delivery.addDrink(tea);

        // Delivery → Drinks
        assertEquals(2, delivery.getDrinks().size());
        assertTrue(delivery.getDrinks().contains(coffee));
        assertTrue(delivery.getDrinks().contains(tea));

        // Drink → Deliveries (inverse relationship)
        assertEquals(1, coffee.getDeliveries().size());
        assertEquals(1, tea.getDeliveries().size());
        assertTrue(coffee.getDeliveries().contains(delivery));
        assertTrue(tea.getDeliveries().contains(delivery));
    }
}