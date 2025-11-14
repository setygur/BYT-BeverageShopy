package tests.models;

import models.Fruit;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FruitTests {

    @Test
    void throws_whenListEmpty() {
        assertThrows(ValidationException.class,
                () -> new Fruit(Collections.emptyList(), true));
    }
}
