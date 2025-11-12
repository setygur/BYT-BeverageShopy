package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Shift implements Validatable {
    @ObjectList
    public static List<Shift> shifts = new ArrayList<>();

    @NotBlank
    private String timeOfOrder; // as in UML
    @NotBlank
    private String cashier;

    public Shift(String timeOfOrder, String cashier) {
        this.timeOfOrder = timeOfOrder;
        this.cashier = cashier;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}