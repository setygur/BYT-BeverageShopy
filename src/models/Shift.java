package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Shift implements Validatable {

    // global storage
    @ObjectList
    public static final List<Shift> shifts = new ArrayList<>();

    // mandatory many-to-many
    @ObjectList
    private final List<Facility> facilities = new ArrayList<>(); // 1..*

    // optional many-to-many
    @ObjectList
    private final List<Employee> employees = new ArrayList<>();  // 0..*

    @NotNull
    private LocalDateTime beginningTime;

    @NotNull
    private LocalDateTime endTime;

    @JsonCtor
    public Shift(LocalDateTime beginningTime, LocalDateTime endTime) {
        this.beginningTime = beginningTime;
        this.endTime = endTime;

        // validate basic fields only (NotNull, Range, etc.)
        try {
            if (!validate(this)) throw new ValidationException("Invalid shift data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        shifts.add(this);
    }

    // -------- Facility relation (1..*) --------

    protected void internalAddFacility(Facility f) {
        if (!facilities.contains(f)) facilities.add(f);
    }

    protected void internalRemoveFacility(Facility f) {
        facilities.remove(f);
    }

    public void addFacility(Facility facility) {
        if (!facilities.contains(facility)) {
            facilities.add(facility);
            facility.addShift(this);
        }
    }

    public void removeFacility(Facility facility) {
        if (facilities.remove(facility)) {
            facility.removeShift(this);
        }
    }

    public void validate() {
        if (facilities.isEmpty())
            throw new ValidationException("Shift must belong to at least one Facility");
    }

    public List<Facility> getFacilities() {
        return Collections.unmodifiableList(facilities);
    }

    // -------- Employee relation (0..*) --------

    public void addEmployee(Employee e) {
        if (!employees.contains(e)) {
            employees.add(e);
            e.internalAddShift(this);
        }
    }

    public void removeEmployee(Employee e) {
        if (employees.remove(e)) {
            e.internalRemoveShift(this);
        }
    }

    public List<Employee> getEmployees() {
        return Collections.unmodifiableList(employees);
    }

    // -------- Derived attribute --------

    public double getDuration() {
        if (beginningTime == null || endTime == null) return 0.0;

        double hours = Duration.between(beginningTime, endTime).toHours();
        if (hours < 0)
            throw new ValidationException("Shift duration must be positive");

        return hours;
    }
}