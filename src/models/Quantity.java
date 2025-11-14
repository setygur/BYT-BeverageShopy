package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Quantity implements Validatable {
    @ObjectList
    public static List<Quantity> quantitys = new ArrayList<>();

    public Quantity() {
        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        quantitys.add(this);
    }
}