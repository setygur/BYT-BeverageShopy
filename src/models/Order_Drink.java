package models;

import models.utils.Drink_Size;
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
    @Derived
    private double additionalCost;

    public Order_Drink(boolean heated, boolean cooled, Drink_Size size) {
        this.heated = heated;
        this.cooled = cooled;
        this.size = size;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.additionalCost = 0.0; // TODO derive after validation
    }
}