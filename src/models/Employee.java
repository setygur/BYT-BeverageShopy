package models;

import validation.EitherOr;

public class Employee extends Person {
    @EitherOr("passportNumber")
    private String peselNumber;
    @EitherOr("peselNumber")
    private String passportNumber;
    private static double baseSalary;
}
