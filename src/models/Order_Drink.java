package models;

import models.utils.Drink_Size;
import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Order_Drink implements Validatable {
    @ObjectList
    public static List<Order_Drink> order_Drinks = new ArrayList<>();
    @NotNull
    private boolean heated;
    @NotNull
    private boolean cooled;
    @NotNull
    private Drink_Size size;
    @JsonIgnore
    @Derived
    private double additionalCost;
    private Drink drink;
    private List<String> toppings = new ArrayList<>();

    @JsonCtor
    public Order_Drink(boolean heated, boolean cooled, Drink_Size size) {
        this.heated = heated;
        this.cooled = cooled;
        this.size = size;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        double cost = drink.basePrice;

        switch (size) {
            case MEDIUM: cost += 2; break;
            case BIG: cost += 4; break;
            case XXL: cost += 6; break;
            default: cost += 0;
        }

        cost += toppings.size() * 1.0;

        this.additionalCost = cost;
        order_Drinks.add(this);
    }
}