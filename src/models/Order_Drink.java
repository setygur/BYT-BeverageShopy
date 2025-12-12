package models;

import models.utils.Drink_Size;
import models.utils.OrderQualifier;
import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Order_Drink implements Validatable {
    @ObjectList
    public static List<Order_Drink> order_Drinks = new ArrayList<>();
    @NotNull
    private boolean heated;
    @NotNull
    private boolean cooled;
    @NotNull
    private Drink_Size size;
    @JsonIgnore
    @Derived
    private double additionalCost;
    private Drink drink;
    private Order order;
    @NotNull
    private List<String> toppings = new ArrayList<>();

    @JsonCtor
    public Order_Drink(Order order, Drink drink, boolean heated, boolean cooled, Drink_Size size, List<String> toppings) {
        this.drink = drink;
        this.heated = heated;
        this.cooled = cooled;
        this.size = size;
        this.toppings = toppings;
        this.order = order;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        double cost = drink.basePrice;

        switch (size) {
            case MEDIUM: cost += 2; break;
            case BIG: cost += 4; break;
            case XXL: cost += 6; break;
            default: cost += 0;
        }

        cost += toppings.size() * 1.0;

        this.additionalCost = cost;
        order_Drinks.add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order_Drink)) return false;
        Order_Drink that = (Order_Drink) o;
        return Objects.equals(heated, that.heated) &&
                Objects.equals(cooled, that.cooled) &&
                Objects.equals(size, that.size) &&
                Objects.equals(drink, that.drink) &&
                Objects.equals(additionalCost, that.additionalCost) &&
                Objects.equals(toppings, that.toppings);
    }

    public static Order_Drink find(Order order, Drink drink, boolean heated, boolean cooled,
                                   Drink_Size size,  List<String> toppings) {
        for (Order_Drink od : order_Drinks) {
            if(od.getOrder() == order) {
                if(od.getDrink() == drink) {
                    if(od.isHeated() == heated) {
                        if(od.isCooled() == cooled) {
                            if(od.getSize() == size) {
                                if(od.getToppings() ==  toppings) {
                                    return od;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isHeated() {
        return heated;
    }

    public void setHeated(boolean heated) {
        this.heated = heated;
    }

    public boolean isCooled() {
        return cooled;
    }

    public void setCooled(boolean cooled) {
        this.cooled = cooled;
    }

    public Drink_Size getSize() {
        return size;
    }

    public void setSize(Drink_Size size) {
        this.size = size;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }
}