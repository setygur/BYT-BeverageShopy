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
    @JsonIgnore
    @Derived
    private double totalPrice;

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
        this.totalPrice = 0.0; // TODO derive after validation
        orders.add(this);
    }
}