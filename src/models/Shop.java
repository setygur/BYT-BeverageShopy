package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@JsonSerializable
public class Shop implements Validatable {
    @ObjectList
    public static List<Shop> shops = new ArrayList<>();

    @Derived
    private int salesNum;
    @NotNull
    @NotFuture
    private LocalDateTime dateOfLastStock;

    @JsonCtor
    public Shop(LocalDateTime dateOfLastStock) {
        this.dateOfLastStock = dateOfLastStock;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        this.salesNum = 0;

        shops.add(this);
    }

    public int getDaysFromLastStock() {
        if (dateOfLastStock == null) return 0;
        return (int) ChronoUnit.DAYS.between(dateOfLastStock.toLocalDate(), LocalDate.now());
    }
}