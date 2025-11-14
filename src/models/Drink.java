package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Drink implements Validatable {
    @ObjectList
    public static List<Drink> drinks = new ArrayList<>();

    @NotBlank
    private String name;
    @NotNull
    @Range(min = 0)
    public double basePrice;
    @NotBlank
    private String persistentAlergens;

    //Drink types
    private Coffee coffee;
    private Tea tea;
    private Milk milk;
    private Fruit fruit;

    @JsonCtor
    public Drink(String name, double basePrice, String persistentAlergens, Coffee coffee, Tea tea, Milk milk, Fruit fruit) {
        this.name = name;
        this.basePrice = basePrice;
        this.persistentAlergens = persistentAlergens;
        this.coffee = coffee;
        this.tea = tea;
        this.milk = milk;
        this.fruit = fruit;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        drinks.add(this);
    }
}