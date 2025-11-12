package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Fruit implements Validatable {
    @ObjectList
    public static List<Fruit> fruits = new ArrayList<>();

    @NotBlank
    private String fruit;
    @NotNull
    private boolean pulp;

    public Fruit(String fruit, boolean pulp) {
        this.fruit = fruit;
        this.pulp = pulp;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}