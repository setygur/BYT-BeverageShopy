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
    private List<Employee> managed = new ArrayList<>();
    private List<Employee> trained = new ArrayList<>();

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

    public void addManaged(Employee managed){
        if(managed == null) throw new ValidationException("Invalid data");
        if(this.managed.contains(managed)) return;
        this.managed.add(managed);
        managed.setManager(this);
    }

    public void removeManaged(Employee managed){
        if(managed == null) throw new ValidationException("Invalid data");
        if(!this.managed.contains(managed)) return;
        this.managed.remove(managed);
        managed.removeManager(this);
    }

    public void setManaged(Employee oldManaged, Employee newManaged){
        if(oldManaged == null) throw new ValidationException("Invalid data");
        if(newManaged == null) throw new ValidationException("Invalid data");
        if(this.managed.contains(oldManaged)){
            this.managed.remove(oldManaged);
            oldManaged.removeManager(this);
        }
        if(!this.managed.contains(newManaged)){
            this.managed.add(newManaged);
            newManaged.addManager(this);
        }
    }

    public void addTrainee(Employee trained){
        if(trained == null) throw new ValidationException("Invalid data");
        if(this.trained.contains(trained)) return;
        if(this.managed.contains(trained)) {
            this.trained.add(trained);
            trained.addManager(this);
        } else {
            throw new ValidationException("This Trainer does not manage this Employee");
        }
    }

    public void removeTrainee(Employee trained){
        if(trained == null) throw new ValidationException("Invalid data");
        if(!this.trained.contains(trained)) return;
        this.trained.remove(trained);
        trained.removeTrainer(this);
    }

    public void setTrainee(Employee oldTrained, Employee newTrained){
        if(oldTrained == null) throw new ValidationException("Invalid data");
        if(newTrained == null) throw new ValidationException("Invalid data");
        if(this.trained.contains(oldTrained)){
            this.trained.remove(oldTrained);
            oldTrained.removeTrainer(this);
        }
        if(!this.trained.contains(newTrained)){
            if(this.managed.contains(newTrained)) {
                this.trained.add(newTrained);
                newTrained.addTrainer(this);
            } else {
                throw new ValidationException("This Trainer does not manage this Employee");
            }
        }
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