package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.NotBlank;
import validation.Unique;
import validation.Validatable;
import validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@JsonSerializable
public abstract class Person implements Validatable {
    @ObjectList
    public static List<Person> Person = new ArrayList<>();

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

//        try {
//            if (!validate(this)) throw new ValidationException("Invalid data");
//        } catch (IllegalAccessException | ValidationException e) {
//            throw new ValidationException(e.getMessage());
//        }
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        sj.add(this.name);
        sj.add(this.surname);
        sj.add(this.email);
        return sj.toString();
    }
}
