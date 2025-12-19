package models;

import models.aspects.SweetenerAspect;
import models.aspects.TemperatureAspect;
import models.utils.Drink_Size;
import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.util.*;

@JsonSerializable
public class Drink implements Validatable {
    @ObjectList
    public static List<Drink> drinks = new ArrayList<>();

    @NotBlank
    private String name;

    @NotNull
    @Range(min = 0)
    public double basePrice;

    @NotBlank
    private String persistentAllergens;

    private final List<Order_Drink> orders = new ArrayList<>();

    // Drink types (overlapping, complete assumed)
    private Coffee coffee;
    private Tea tea;
    private Milk milk;
    private Fruit fruit;

    // Deliveries 0..*
    private final List<Delivery> deliveries = new ArrayList<>();

    // ---------------- Constructor ----------------

    @JsonCtor
    public Drink(
            String name,
            double basePrice,
            String persistentAllergens,
            Coffee coffee,
            Tea tea,
            Milk milk,
            Fruit fruit
    ) {
        this.name = name;
        this.basePrice = basePrice;
        this.persistentAllergens = persistentAllergens;
        this.coffee = coffee;
        this.tea = tea;
        this.milk = milk;
        this.fruit = fruit;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        drinks.add(this);
    }

    // ---------------- Orders (association class) ----------------

    public void addOrder(
            Order order,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) throws IllegalAccessException {
        if (order == null) throw new ValidationException("Invalid data");
        if (temperature == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");

        Set<SweetenerAspect> safeSweeteners =
                (sweeteners == null) ? Collections.emptySet() : new HashSet<>(sweeteners);

        List<String> safeToppings = new ArrayList<>(toppings);

        Order_Drink od = Order_Drink.find(
                order,
                this,
                temperature,
                safeSweeteners,
                size,
                safeToppings
        );

        if (od == null) {
            od = new Order_Drink(
                    order,
                    this,
                    temperature,
                    safeSweeteners,
                    size,
                    safeToppings
            );
        }

        addOrder(od);
    }

    public void addOrder(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if (orders.contains(od)) return;

        orders.add(od);
        od.getOrder().addDrink(od); // sync inverse
    }

    public void removeOrder(
            Order order,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        if (order == null) throw new ValidationException("Invalid data");
        if (temperature == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");

        Set<SweetenerAspect> safeSweeteners =
                (sweeteners == null) ? Collections.emptySet() : new HashSet<>(sweeteners);

        Order_Drink od = Order_Drink.find(
                order,
                this,
                temperature,
                safeSweeteners,
                size,
                toppings
        );

        if (od != null) {
            removeOrder(od);
        }
    }

    public void removeOrder(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if (!orders.contains(od)) return;

        orders.remove(od);
        od.getOrder().removeDrink(od); // sync inverse
    }

    public void setOrder(Order_Drink oldOd, Order_Drink newOd) {
        if (oldOd == null) throw new ValidationException("Invalid data");
        if (newOd == null) throw new ValidationException("Invalid data");
        if (!orders.contains(oldOd)) return;

        orders.remove(oldOd);
        orders.add(newOd);

        oldOd.getOrder().setDrink(oldOd, newOd); // sync inverse
    }

    // ---------------- Deliveries ----------------

    public void addDelivery(Delivery delivery) {
        if (delivery != null && !deliveries.contains(delivery)) {
            deliveries.add(delivery);
            delivery.addDrink(this);
        }
    }

    public void removeDelivery(Delivery delivery) {
        if (delivery != null && deliveries.contains(delivery)) {
            deliveries.remove(delivery);
            delivery.removeDrink(this);
        }
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
}