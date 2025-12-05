package modelsTests;


import models.Drink;
import models.Order_Drink;
import models.utils.Drink_Size;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class Order_DrinkTests {
    private Drink drink =  new Drink("TestDrink", 15.0, "None",
            null, null, null, null);

    @Test
    void throws_whenNoSizeProvided() {
        assertThrows(ValidationException.class,
                () -> new Order_Drink(drink,true, false, null));
    }


    // TEST does not pass

    @Test
    void createsOrderDrink_whenSizeProvided() {
        Order_Drink od = assertDoesNotThrow(() ->
                new Order_Drink(drink,true, false, Drink_Size.MEDIUM)
        );
        assertNotNull(od);
    }
}
