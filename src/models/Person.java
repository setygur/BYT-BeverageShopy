package models;

import validation.NotBlank;
import validation.Unique;

public class Person {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Unique
    private String email;
}
