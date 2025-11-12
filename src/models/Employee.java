package models;

import persistence.JsonSerializable;
import validation.EitherOr;

@JsonSerializable
public class Employee extends Person {
    @EitherOr("passportNumber")
    private String peselNumber;
    @EitherOr("peselNumber")
    private String passportNumber;
    private static double baseSalary;
}
