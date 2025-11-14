package tests.models;

import models.*;
import models.utils.TypeOfMilk;
import models.utils.TypeOfTea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DrinkTests {

    @BeforeEach
    void reset() {
        resetObjectLists(Drink.class, Coffee.class, Tea.class, Milk.class, Fruit.class);
    }

    @Test
    void throws_whenPriceNegative() {
        assertThrows(ValidationException.class, () ->
                new Drink(
                        "Sunrise",
                        -1.0,
                        "nuts",
                        new Coffee(5),
                        new Tea(TypeOfTea.GREEN),
                        new Milk(TypeOfMilk.OAT),
                        new Fruit(Collections.singletonList("Apple"), true)
                )
        );
    }

    private void resetObjectLists(Class<?>... classes) {
        for (Class<?> c : classes) {
            for (Field f : c.getDeclaredFields()) {
                try {
                    if (f.isAnnotationPresent(persistence.ObjectList.class)
                            && List.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        List<?> list = (List<?>) f.get(null);
                        if (list != null) list.clear();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
