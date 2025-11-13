package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class FrequentCustomer extends Person{
    @ObjectList
    public static List<FrequentCustomer> frequentCustomers = new ArrayList<>();
    @NotBlank
    private String phoneNumber;
    private static double baseDiscount;
    @NotNull
    private int amountOfOrders;
    @Derived
    private double calculatedDiscount;


    @JsonCtor
    public FrequentCustomer(String name, String surname, String email, String phoneNumber,  int amountOfOrders) {
        super(name, surname, email);
        this.phoneNumber = phoneNumber;
        this.amountOfOrders = amountOfOrders;

        try{
            if(!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        this.calculatedDiscount = 0.0; //TODO add derived logic after validation
        frequentCustomers.add(this);
    }
}
