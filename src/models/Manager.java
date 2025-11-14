package models;

import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Manager extends Employee {
    @ObjectList
    public static List<Manager> managers = new ArrayList<>();

    @NotNull
    private double managerEvaluationScore;
    @NotNull
    private double bonusPercent;
    @JsonIgnore
    @Derived
    private double salary;

    public Manager(String name, String surname, String email, String peselNumber, String passportNumber, double managerEvaluationScore, double bonusPercent) {
        super(name, surname, email, peselNumber, passportNumber);
        this.managerEvaluationScore = managerEvaluationScore;
        this.bonusPercent = bonusPercent;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salary = 0.0; // TODO derive after validation
        managers.add(this);
    }
}