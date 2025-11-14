package models;

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
    private double baseprice;
    @NotBlank
    private String persistentalergens;

    //Drink types
    private Coffee coffee;
    private Tea tea;
    private Milk milk;
    private Fruit fruit;

    public Drink(String name, String persistentalergens, Coffee coffee, Tea tea, Milk milk, Fruit fruit) {
        this.name = name;
        this.persistentalergens = persistentalergens;
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