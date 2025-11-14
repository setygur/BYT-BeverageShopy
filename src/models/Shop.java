package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Shop implements Validatable {
    @ObjectList
    public static List<Shop> shops = new ArrayList<>();

    @Derived
    private int salesNum;
    @NotNull
    private LocalDateTime dateOfLastStock;
    @Derived
    private int daysFromLastStock;

    public Shop(LocalDateTime dateOfLastStock) {
        this.dateOfLastStock = dateOfLastStock;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salesNum = 0; // TODO derive after validation
        this.daysFromLastStock = 0; // TODO derive after validation
    }
}