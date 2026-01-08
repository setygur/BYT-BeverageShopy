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
    private Facility facility; // The owner

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
    public Warehouse(Facility facility, double capacity, boolean temperatureControlled) {
        if (facility == null) {
            throw new ValidationException("Warehouse cannot exist without a Certificate (Composition)");
        }

        this.facility = facility;
        this.capacity = capacity;
        this.temperatureControlled = temperatureControlled;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }
        this.facility.addWarehouse(this);
        warehouses.add(this);
    }

    public double getAvailableCapacity() {
        double tmpCapacity = this.capacity;
        for(Delivery delivery : deliveries) {
            double deliveryCapacity = delivery.getCapacityKg();
            if (deliveryCapacity > tmpCapacity) {
                throw new ValidationException("Delivery capacity is greater than the total capacity");
            } else {
                tmpCapacity -= deliveryCapacity;
            }
        }
        availableCapacity = tmpCapacity;

        return availableCapacity;
    }

    public void addDelivery(Delivery d) {
        deliveries.add(d);
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void removeConnection() {
        if (facility != null) {
            Facility tmp = facility;
            facility = null;
            tmp.removeWarehouse();
            warehouses.remove(this);
        }
    }
}