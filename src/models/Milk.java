package models;

import models.utils.TypeOfMilk;
import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Milk implements Validatable {
    @ObjectList
    public static List<Milk> milks = new ArrayList<>();

    @NotNull
    private TypeOfMilk typeOfMilk;

    @JsonCtor
    public Milk(TypeOfMilk typeOfMilk) {
        this.typeOfMilk = typeOfMilk;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}