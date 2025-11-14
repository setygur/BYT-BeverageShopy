package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.NotNull;
import validation.Range;
import validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@JsonSerializable
public class Employee extends Person {
    @ObjectList
    public static List<Employee> employees = new ArrayList<>();
    private String peselNumber;
    private String passportNumber;
    @Range(min = 0)
    private static double baseSalary;


    @JsonCtor
    public Employee(String name, String surname, String email, String peselNumber, String passportNumber){
        super(name, surname, email);
        this.peselNumber = peselNumber;
        this.passportNumber = passportNumber;

        if(peselNumber == null && passportNumber == null){
            throw new ValidationException("Either pesel number or passport number must be present");
        }

        try{
            if(!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        employees.add(this);
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
