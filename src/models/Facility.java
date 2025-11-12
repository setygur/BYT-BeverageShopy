package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Facility implements Validatable {
    @ObjectList
    public static List<Facility> facilitys = new ArrayList<>();

    @NotBlank
    private String address;

    public Facility(String address) {
        this.address = address;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}