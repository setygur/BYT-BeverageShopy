package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.EitherOr;
import validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@JsonSerializable
public class Employee extends Person{
    @ObjectList
    public static List<Employee> employees = new ArrayList<>();
    @EitherOr(dependsOn = "passportNumber")
    private String peselNumber;
    @EitherOr(dependsOn = "peselNumber")
    private String passportNumber;
    private static double baseSalary;

    public Employee(String name, String surname, String email, String peselNumber, String passportNumber){
        super(name, surname, email);
        this.peselNumber = peselNumber;
        this.passportNumber = passportNumber;

        try{
            if(!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public String toString(){
        StringJoiner sj = new StringJoiner(", ");
        sj.add(super.toString());
        sj.add(this.peselNumber);
        sj.add(this.passportNumber);
        return sj.toString();
    }
}
