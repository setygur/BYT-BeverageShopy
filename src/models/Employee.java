package models;

import persistence.JsonCtor;
import validation.Range;
import validation.ValidationException;

import java.util.StringJoiner;

public abstract class Employee extends Person {
    private String peselNumber;
    private String passportNumber;
    @Range(min = 0)
    private static double baseSalary;

    private Manager manager;
    private Manager trainer;


    @JsonCtor
    public Employee(String name, String surname, String email, String peselNumber, String passportNumber){
        super(name, surname, email);
        this.peselNumber = peselNumber;
        this.passportNumber = passportNumber;

        if(peselNumber == null && passportNumber == null){
            throw new ValidationException("Either pesel number or passport number must be present");
        }
    }

    public void addManager(Manager manager) {
        if(manager == null) throw new ValidationException("Invalid data");
        if(this.manager != null){
            this.manager.removeManaged(this);
        }
        this.manager = manager;
        manager.addManaged(this);
    }

    public void removeManager(Manager manager) {
        if(manager == null) throw new ValidationException("Invalid data");
        if(this.manager != null){
            Manager managerToRemove = this.manager;
            this.manager = null;
            managerToRemove.removeManaged(this);
        }
    }

    public void setManager(Manager manager) {
        if(manager == null) throw new ValidationException("Invalid data");
        if(this.manager != null && this.manager != manager){
            this.manager.removeManaged(this);
        }
        if(this.manager != manager){
            this.manager = manager;
            manager.addManaged(this);
        }
    }

    public void addTrainer(Manager trainer) {
        if(trainer == null) throw new ValidationException("Invalid data");
        if(this.trainer != null){
            Manager oldTrainer = this.trainer;
            this.trainer.removeTrainee(this);
            oldTrainer.removeTrainee(this);
        }
        if(this.trainer == trainer) return;
        this.trainer = trainer;
        trainer.addTrainee(this);
    }

    public void removeTrainer(Manager trainer) {
        if(trainer == null) throw new ValidationException("Invalid data");
        if(this.trainer != null && this.trainer == trainer){
            this.trainer = null;
            trainer.removeTrainee(this);
        }
    }

    public void setTrainer(Manager oldTrainer,  Manager newTrainer) {
        if(oldTrainer == null) throw new ValidationException("Invalid data");
        if(newTrainer == null) throw new ValidationException("Invalid data");
        if(this.trainer != null && this.trainer != newTrainer){
            if(this.trainer == oldTrainer){
                oldTrainer.removeTrainee(this);
                this.trainer = newTrainer;
                newTrainer.addTrainee(this);
            }
        }
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        sj.add(super.toString());
        sj.add(this.peselNumber);
        sj.add(this.passportNumber);
        return sj.toString();
    }

    public static void setBaseSalary(double baseSalary) {
        Employee.baseSalary = baseSalary;
    }
}
