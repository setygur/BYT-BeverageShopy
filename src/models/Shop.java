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
    @JsonIgnore
    @Derived
    private int daysFromLastStock;

    @JsonCtor
    public Shop(LocalDateTime dateOfLastStock) {
        this.dateOfLastStock = dateOfLastStock;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        LocalDate last = LocalDate.parse(dateOfLastStock.toString());
        LocalDate now = LocalDate.now();

        this.daysFromLastStock = (int) ChronoUnit.DAYS.between(last, now);
        this.salesNum = 0;

        shops.add(this);
    }
}