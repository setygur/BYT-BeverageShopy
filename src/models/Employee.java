package models;

import persistence.JsonCtor;
import persistence.ObjectList;
import validation.NotNull;
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

    @ObjectList
    private final List<Shift> shifts = new ArrayList<>(); // 0..*

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