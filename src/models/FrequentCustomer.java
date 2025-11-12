package models;

import persistence.JsonSerializable;
import validation.Derived;
import validation.NotBlank;
import validation.NotNull;

@JsonSerializable
public class FrequentCustomer extends Person {
    @NotBlank
    private String phoneNumber;
    private static double baseDiscount;
    @NotNull
    private int amountOfOrders;
    @Derived
    private double calculatedDiscount;
}
