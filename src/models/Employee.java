package models;

import persistence.JsonSerializable;
import validation.EitherOr;
import validation.ValidationException;

@JsonSerializable
public class Employee extends Person{
    @EitherOr("passportNumber")
    private String peselNumber;
    @EitherOr("peselNumber")
    private String passportNumber;
    private static double baseSalary;

    public Employee(String name, String surname, String email, String peselNumber, String passportNumber) {
        super(name, surname, email);
        this.peselNumber = peselNumber;
        this.passportNumber = passportNumber;

        if(!validate(this)) throw new ValidationException("Invalid data");
    }
}
