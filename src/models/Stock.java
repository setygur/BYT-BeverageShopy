package models;

import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Stock implements Validatable {
    @ObjectList
    public static List<Stock> stocks = new ArrayList<>();

    @NotBlank
    private LocalDateTime lastUpdated;
    @JsonIgnore
    @Derived
    private double salePrice;

    public Stock(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salePrice = 0.0; // TODO derive after validation
        stocks.add(this);
    }
}