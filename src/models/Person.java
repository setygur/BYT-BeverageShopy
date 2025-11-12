package models;

import persistence.JsonSerializable;
import validation.NotBlank;
import validation.Unique;
import validation.Validatable;
import validation.ValidationException;

@JsonSerializable
public class Person implements Validatable{
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Unique
    private String email;

    public Person(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;

        //TODO add custom validation for email

        if(!validate(this)) throw new ValidationException("Invalid data");
    }
}
