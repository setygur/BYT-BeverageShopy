package models;

import models.utils.Address;
import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Facility implements Validatable {
    @ObjectList
    public static List<Facility> facilities = new ArrayList<>();

    @NotNull
    private Address address;

    @NotNull
    private Stock stock;

    @JsonCtor
    public Facility(Address address) {
        this.address = address;
        this.stock = new Stock(LocalDateTime.now(), this);

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        facilities.add(this);
    }

    public Stock getStock() {
        return stock;
    }

    public Address getAddress() {
        return address;
    }
}