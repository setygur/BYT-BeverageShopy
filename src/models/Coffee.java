package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Coffee implements Validatable {
    @ObjectList
    public static List<Coffee> coffees = new ArrayList<>();

    @NotNull
    @Range(min = 1, max = 10)
    private int caffeineLevel;

    @JsonCtor
    public Coffee(int caffeineLevel) {
        this.caffeineLevel = caffeineLevel;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        coffees.add(this);
    }
}