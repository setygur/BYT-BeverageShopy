package models;

import persistence.JsonCtor;
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
    @Range(min = 0)
    private double managerEvaluationScore;
    @NotNull
    private double bonusPercent;

    @JsonCtor
    public Manager(String name, String surname, String email, String peselNumber, String passportNumber, double managerEvaluationScore, double bonusPercent) {
        super(name, surname, email, peselNumber, passportNumber);
        this.managerEvaluationScore = managerEvaluationScore;
        this.bonusPercent = bonusPercent;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        managers.add(this);
    }

    private double getSpentOnShifts() {
        return 1000.0;
    }

    private double getTrainingBonus() {
        return 200.0;
    }

    public double getSalary() {
        // salary = spentOnShifts + bonusForTraining + managerPercentage
        double base = getSpentOnShifts() + getTrainingBonus();
        return base + (base * (bonusPercent / 100.0));
    }
}