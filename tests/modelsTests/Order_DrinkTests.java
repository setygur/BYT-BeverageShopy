package tests.modelsTests;


import models.Order_Drink;
import models.utils.Drink_Size;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class Order_DrinkTests {

    @Test
    void throws_whenNoSizeProvided() {
        assertThrows(ValidationException.class,
                () -> new Order_Drink(true, false, null));
    }


    // TEST does not pass

    @Test
    void createsOrderDrink_whenSizeProvided() {
        Order_Drink od = assertDoesNotThrow(() ->
                new Order_Drink(true, false, Drink_Size.MEDIUM)
        );
        assertNotNull(od);
    }
}
