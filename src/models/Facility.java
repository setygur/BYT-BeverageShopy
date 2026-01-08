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
    public static final List<Facility> facilities = new ArrayList<>();

    private Warehouse warehouse; // 0..1 composed Warehouse

    @ObjectList
    private final List<Shift> shifts = new ArrayList<>(); // 1..*

    @NotNull
    private Address address;

    @NotNull
    private Stock stock;

    @JsonCtor
    public Facility(Address address) {
        this.address = address;
        this.stock = new Stock(LocalDateTime.now(), this);

        try {
            if (!validate(this)) throw new ValidationException("Invalid facility data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // -------- Shift relation (1..*) --------

    public void addShift(Shift shift) {
        if (shift == null) throw new IllegalArgumentException("Shift cannot be null");

        if (!shifts.contains(shift)) {
            shifts.add(shift);
            if (!shift.getFacilities().contains(this)) {
                shift.addFacility(this);
            }
        }
    }

    public void removeShift(Shift shift) {
        if (shifts.remove(shift)) {
            if (shift.getFacilities().contains(this)) {
                shift.removeFacility(this);
            }
        }
    }

    public void validate() {
        if (shifts.isEmpty())
            throw new ValidationException("Facility must have at least one Shift");
    }

    public List<Shift> getShifts() {
        return Collections.unmodifiableList(shifts);
    }

    public Stock getStock() {
        return stock;
    }

    public Address getAddress() {
        return address;
    }

    public void addWarehouse(Warehouse warehouse) {
        if (warehouse == null) throw new ValidationException("Invalid data");

        if (this.warehouse != null)
            throw new ValidationException("Facility can have only one Warehouse");

        this.warehouse = warehouse;
    }

    public void removeWarehouse() {
        if (this.warehouse != null) {
            this.warehouse.removeConnection();
            this.warehouse = null;
        }
    }

    public Optional<Warehouse> getWarehouse() {
        return Optional.ofNullable(warehouse);
    }
}