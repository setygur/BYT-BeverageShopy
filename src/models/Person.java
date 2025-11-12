package models;

import persistence.JsonSerializable;
import validation.NotBlank;
import validation.Unique;
import validation.Validatable;
import validation.ValidationException;

import java.util.StringJoiner;

@JsonSerializable
public abstract class Person implements Validatable{
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

        try{
            if(!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public String toString(){
        StringJoiner sj = new StringJoiner(", ");
        sj.add(this.name);
        sj.add(this.surname);
        sj.add(this.email);
        return sj.toString();
    }
}
