package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import validation.NotBlank;
import validation.Unique;
import validation.Validatable;
import validation.ValidationException;

import java.util.StringJoiner;


@JsonSerializable
public abstract class Person implements Validatable {

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Unique
    private String email;


    @JsonCtor
    public Person(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;

        //TODO add custom validation for email
    }

    // Method for Employee
    public void removeConnection() {
        
    }

    // helper method
    public final void destroy() {
        removeConnection();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        sj.add(this.name);
        sj.add(this.surname);
        sj.add(this.email);
        return sj.toString();
    }

    //generic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new ValidationException("Name cannot be blank");
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        if (surname == null || surname.isBlank()) throw new ValidationException("Surname cannot be blank");
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) throw new ValidationException("Email cannot be blank");
        this.email = email;
    }
}
