package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Order implements Validatable {
    @ObjectList
    public static List<Order> orders = new ArrayList<>();

    @NotBlank
    private String orderId;
    @NotBlank
    private String timeOfOrder;
    @NotBlank
    private String name;
    @NotBlank
    private String tip;
    @Derived
    private double totalPrice;

    public Order(String orderId, String timeOfOrder, String name, String tip) {
        this.orderId = orderId;
        this.timeOfOrder = timeOfOrder;
        this.name = name;
        this.tip = tip;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.totalPrice = 0.0; // TODO derive after validation
    }
}