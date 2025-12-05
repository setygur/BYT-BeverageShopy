package models;

import models.utils.Drink_Size;
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
    private List<Order_Drink> drinks = new ArrayList<>();

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

    public void addDrinkToOrder(Drink drink, boolean heated, boolean cooled, Drink_Size size) {
        drinks.add(new Order_Drink(drink, heated, cooled, size));
    }

    public void removeDrinkFromOrder(Drink drink, boolean heated, boolean cooled, Drink_Size size) {
        for (Order_Drink o : drinks) {
            if(Order_Drink.order_Drinks.equals(new Order_Drink(drink, heated, cooled, size))){
                drinks.remove(o);
                return;
            }
        }
    }
}