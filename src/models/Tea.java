package models;

import models.utils.TypeOfTea;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Tea implements Validatable {
    @ObjectList
    public static List<Tea> teas = new ArrayList<>();

    @NotBlank
    private TypeOfTea typeOfTea;

    public Tea(TypeOfTea typeOfTea) {
        this.typeOfTea = typeOfTea;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}