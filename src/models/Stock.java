package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Stock implements Validatable {
    @ObjectList
    public static List<Stock> stocks = new ArrayList<>();

    @NotBlank
    private String lastUpdated;
    @Derived
    private double salePrice;

    public Stock(String lastUpdated) {
        this.lastUpdated = lastUpdated;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salePrice = 0.0; // TODO derive after validation
    }
}