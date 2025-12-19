package models;

import models.utils.EmployeeType;
import models.utils.Status;
import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

import static models.Drink.drinks;

@JsonSerializable
public class Delivery implements Validatable {

    @ObjectList
    public static List<Delivery> deliveries = new ArrayList<>();

    @NotBlank
    @NotFuture
    @NotNull
    private LocalDateTime timeStarted;

    private LocalDateTime timeDelivered;

    @NotNull
    private double capacityKg;

    @NotNull
    private Status status;

    private List<Employee> loaders = new ArrayList<>();

    // The warehouse this delivery was taken from (0..1)
    private Warehouse sourceWarehouse;

    // Drinks contained at delivery (0..*)
    private List<Drink> drinks = new ArrayList<>();

    public void addDrink(Drink drink) {
        if (drink != null && !drinks.contains(drink)) {
            drinks.add(drink);
            drink.addDelivery(this); // sync inverse
        }
    }

    public void removeDrink(Drink drink) {
        if (drink != null && drinks.contains(drink)) {
            drinks.remove(drink);
            drink.removeDelivery(this); // sync inverse
        }
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    @JsonCtor
    public Delivery(LocalDateTime timeStarted,
                    LocalDateTime timeDelivered,
                    int capacityKg,
                    Status status,
                    Warehouse sourceWarehouse) {

        this.timeStarted      = timeStarted;
        this.timeDelivered    = timeDelivered;
        this.capacityKg       = capacityKg;
        this.status           = status;
        this.sourceWarehouse  = sourceWarehouse;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }

        if (sourceWarehouse != null)
            sourceWarehouse.addDelivery(this);

        deliveries.add(this);
    }

    public Warehouse getSourceWarehouse() {
        return sourceWarehouse;
    }

    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public LocalDateTime getTimeDelivered() {
        return timeDelivered;
    }

    public double getCapacityKg() {
        return capacityKg;
    }

    public Status getStatus() {
        return status;
    }

    public boolean addLoader(Employee loader) {
        if(loader ==  null) throw new ValidationException("Invalid data");

        //Check if employee is Loader
        if(loader.getType() != EmployeeType.LOADER) {
            throw new ValidationException("Employee is not a loader");
        }

        if(loaders.contains(loader)) return true;
        if(loaders.size() >=3) throw new ValidationException("Too many loaders");
        loaders.add(loader);
        loader.addDelivery(this);
        return true;
    }

    public void removeLoader(Employee loader) {
        if(loader ==  null) throw new ValidationException("Invalid data");
        if(!loaders.contains(loader)) return;
        loaders.remove(loader);
        loader.removeDelivery(this);
    }

    public void setLoader(Employee oldLoader, Employee newLoader) {
        if(oldLoader ==  null) throw new ValidationException("Invalid data");
        if(newLoader ==  null) throw new ValidationException("Invalid data");
        if(loaders.contains(oldLoader)) {
            loaders.remove(oldLoader);
            oldLoader.removeDelivery(this);
            if(loaders.size() >=3) throw new ValidationException("Too many loaders");
            loaders.add(newLoader);
            newLoader.addDelivery(this);
        }
    }

    public List<Employee> getLoaders() {
        return Collections.unmodifiableList(loaders);
    }
}
