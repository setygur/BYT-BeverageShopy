package models;

import persistence.JsonSerializable;
import validation.NotBlank;
import validation.Unique;

@JsonSerializable
public class Person {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Unique
    private String email;
}
