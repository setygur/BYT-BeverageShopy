package models;

import persistence.JsonCtor;
import persistence.ObjectList;
import validation.Range;
import validation.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public abstract class Employee extends Person {

    private String peselNumber;
    private String passportNumber;

    @Range(min = 0)
    private static double baseSalary;

    private Manager manager;
    private Manager trainer;

    public List<Shift> shifts = new ArrayList<>();


    @ObjectList
    private final List<Certification> certifications = new ArrayList<>(); // Composition [0..*]

    @JsonCtor
    public Employee(String name, String surname, String email,
                    String peselNumber, String passportNumber) {

        super(name, surname, email);

        this.peselNumber = peselNumber;
        this.passportNumber = passportNumber;

        // custom invariant
        if (peselNumber == null && passportNumber == null)
            throw new ValidationException("Either PESEL or passport must be provided");
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

    public List<Certification> getCertifications() {
        return Collections.unmodifiableList(certifications);
    }

    protected void internalAddCertification(Certification certification) {
        if (!certifications.contains(certification)) {
            certifications.add(certification);
        }
    }

    public void removeCertification(Certification certification) {
        if (certifications.contains(certification)) {
            certifications.remove(certification);
            certification.removeConnection();
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add(super.toString())
                .add(String.valueOf(peselNumber))
                .add(String.valueOf(passportNumber))
                .toString();
    }

    protected void internalAddShift(Shift shift) {
        if (!shifts.contains(shift)) shifts.add(shift);
    }

    protected void internalRemoveShift(Shift shift) {
        shifts.remove(shift);
    }

    public List<Shift> getShifts() {
        return Collections.unmodifiableList(shifts);
    }

    public static void setBaseSalary(double baseSalary) {
        Employee.baseSalary = baseSalary;
    }
}