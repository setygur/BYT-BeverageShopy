package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Fruit implements Validatable {
    @ObjectList
    public static List<Fruit> fruits = new ArrayList<>();

    @NotEmpty
    private List <String> fruit;
    @NotNull
    private boolean pulp;

    @JsonCtor
    public Fruit(List <String> fruit, boolean pulp) {
        this.fruit = fruit;
        this.pulp = pulp;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        fruits.add(this);
    }
}