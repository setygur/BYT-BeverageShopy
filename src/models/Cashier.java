package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Cashier extends Employee {
    @ObjectList
    public static List<Cashier> cashiers = new ArrayList<>();

    @NotNull
    private boolean handlesCash;
    @NotBlank
    @Unique
    private String cashierId;
    @NotNull
    private double cashierEvaluationScore;
    @Derived
    private double salary;

    public Cashier(String name, String surname, String email, String peselNumber, String passportNumber, boolean handlesCash, String cashierId, double cashierEvaluationScore) {
        super(name, surname, email, peselNumber, passportNumber);
        this.handlesCash = handlesCash;
        this.cashierId = cashierId;
        this.cashierEvaluationScore = cashierEvaluationScore;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salary = 0.0; // TODO derive after validation
    }
}