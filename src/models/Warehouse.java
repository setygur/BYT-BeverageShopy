package models;

import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Warehouse implements Validatable {
    @ObjectList
    public static List<Warehouse> warehouses = new ArrayList<>();

    @NotNull
    private double capacity;

    @JsonIgnore
    @Derived
    private double availableCapacity;

    @NotNull
    private boolean temperatureControlled;

    public Warehouse(double capacity, boolean temperatureControlled) {
        this.capacity = capacity;
        this.temperatureControlled = temperatureControlled;
        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        //TODO: implement derived logic
        this.availableCapacity = 0.0;
        warehouses.add(this);
    }
}