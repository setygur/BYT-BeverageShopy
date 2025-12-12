package models;

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
}
