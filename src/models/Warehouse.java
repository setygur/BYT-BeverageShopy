package models;

import persistence.JsonCtor;
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
    @Range(min = 0)
    private double capacity;

    @JsonIgnore
    @Derived
    private double availableCapacity;

    @JsonIgnore   // prevent infinite recursion during serialization
    private List<Delivery> deliveries = new ArrayList<>();

    @NotNull
    private boolean temperatureControlled;

    @JsonCtor
    public Warehouse(double capacity, boolean temperatureControlled) {
        this.capacity = capacity;
        this.temperatureControlled = temperatureControlled;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }
        warehouses.add(this);
    }

    //TODO: getters + setters
    // derived value: one drink takes around 0.3 kg of capacity
    // so we will need to calculate all the deliveries that took the stocks from warehouse
    // !!!!(it is implied that warehouse is full at the beginning as it has no deliveries taken from it)


    public void addDelivery(Delivery d) {
        deliveries.add(d);
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }
}