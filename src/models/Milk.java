package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Milk implements Validatable {
    @ObjectList
    public static List<Milk> milks = new ArrayList<>();

    @NotBlank
    private String typeOfMilk;

    public Milk(String typeOfMilk) {
        this.typeOfMilk = typeOfMilk;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}