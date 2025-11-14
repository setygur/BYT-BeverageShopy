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
    private boolean isCoffee;
    private boolean isTea;
    private boolean isMilk;
    private boolean isFruit;

    public Drink(String name, String persistentalergens, boolean isCoffee, boolean isTea, boolean isMilk, boolean isFruit) {
        this.name = name;
        this.persistentalergens = persistentalergens;
        this.isCoffee = isCoffee;
        this.isTea = isTea;
        this.isMilk = isMilk;
        this.isFruit = isFruit;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}