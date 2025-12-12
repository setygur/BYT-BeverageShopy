package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import validation.NotBlank;
import validation.Unique;
import validation.Validatable;
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

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        sj.add(this.name);
        sj.add(this.surname);
        sj.add(this.email);
        return sj.toString();
    }
}
