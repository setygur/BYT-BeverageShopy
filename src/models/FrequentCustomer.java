package models;

import persistence.JsonSerializable;
import validation.*;

@JsonSerializable
public class FrequentCustomer extends Person{
    @NotBlank
    private String phoneNumber;
    private static double baseDiscount;
    @NotNull
    private int amountOfOrders;
    @Derived
    private double calculatedDiscount;

    public FrequentCustomer(String name, String surname, String email, String phoneNumber,  int amountOfOrders) {
        super(name, surname, email);
        this.phoneNumber = phoneNumber;
        this.amountOfOrders = amountOfOrders;

        if(!validate(this)) throw new ValidationException("Invalid data");

        this.calculatedDiscount = 0.0; //TODO add derived logic after validation
    }
}
