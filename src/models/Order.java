package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Order implements Validatable {
    @ObjectList
    public static List<Order> orders = new ArrayList<>();

    @NotNull
    @Unique
    private long orderId;
    @NotBlank
    @NotFuture
    private LocalDateTime timeOfOrder;
    private double tip;

    @JsonCtor
    public Order(long orderId, LocalDateTime timeOfOrder, double tip) {
        this.orderId = orderId;
        this.timeOfOrder = timeOfOrder;
        this.tip = tip;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        orders.add(this);
    }

    public double getTotalPrice() {
        // Mocked: sum of drink base cost + size cost + toppings cost
        double baseCost = 5.0;
        double sizeCost = 1.0;
        double toppingsCost = 2.0;
        return baseCost + sizeCost + toppingsCost + tip;
    }
}