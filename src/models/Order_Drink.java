package models;

import models.aspects.*;
import models.utils.Drink_Size;
import validation.*;

import java.util.*;

public class Order_Drink implements Validatable {

    public static List<Order_Drink> order_Drinks = new ArrayList<>();

    private final Order order;
    private final Drink drink;

    // === ASPECTS ===
    @NotNull
    private final TemperatureAspect temperature;        // disjoint + complete
    private final Set<SweetenerAspect> sweeteners;       // overlapping + incomplete

    @NotNull
    private final Drink_Size size;

    @NotNull
    private final List<String> toppings;

    @Derived
    private double additionalCost;

    public Order_Drink(
            Order order,
            Drink drink,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        this.order = Objects.requireNonNull(order);
        this.drink = Objects.requireNonNull(drink);
        this.temperature = Objects.requireNonNull(temperature);
        this.size = Objects.requireNonNull(size);
        this.toppings = new ArrayList<>(Objects.requireNonNull(toppings));
        this.sweeteners = (sweeteners == null)
                ? new HashSet<>()
                : new HashSet<>(sweeteners);

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        this.additionalCost = calculateCost();

        order_Drinks.add(this);
    }

    private double calculateCost() {
        double cost = drink.basePrice;

        cost += switch (size) {
            case MEDIUM -> 2;
            case BIG -> 4;
            case XXL -> 6;
            default -> 0;
        };

        cost += toppings.size();
        cost += sweeteners.size() * 0.5;

        return cost;
    }

    // === FIND (FIXED) ===
    public static Order_Drink find(
            Order order,
            Drink drink,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        for (Order_Drink od : order_Drinks) {
            if (Objects.equals(od.order, order) &&
                    Objects.equals(od.drink, drink) &&
                    Objects.equals(od.temperature, temperature) &&
                    Objects.equals(od.size, size) &&
                    Objects.equals(od.toppings, toppings) &&
                    Objects.equals(od.sweeteners, sweeteners)) {
                return od;
            }
        }
        return null;
    }

    // === equality = full identity ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order_Drink od)) return false;

        return Objects.equals(order, od.order) &&
                Objects.equals(drink, od.drink) &&
                Objects.equals(temperature, od.temperature) &&
                Objects.equals(size, od.size) &&
                Objects.equals(toppings, od.toppings) &&
                Objects.equals(sweeteners, od.sweeteners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, drink, temperature, size, toppings, sweeteners);
    }

    public Drink getDrink() {
        return drink;
    }

    public Order getOrder() {
        return order;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public double getAdditionalCost() {
        return additionalCost;
    }
}