package models;

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
    private List<Order_Drink> orders = new ArrayList<>();

    //Drink types
    private Coffee coffee;
    private Tea tea;
    private Milk milk;
    private Fruit fruit;

    @JsonCtor
    public Drink(String name, double basePrice, String persistentAllergens, Coffee coffee, Tea tea,
                 Milk milk, Fruit fruit) {
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

    public void addOrder(Order order, boolean heated, boolean cooled,
                         Drink_Size size, List<String> toppings){
        if (order == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        Order_Drink od = Order_Drink.find(order, this, heated, cooled, size, toppings);
        if(od == null) {
            od = new Order_Drink(order, this, heated, cooled, size, toppings);
        }
        orders.add(od);
        order.addDrink(od);
    }

    public void addOrder(Order_Drink od){
        if(od == null) throw new ValidationException("Invalid data");
        if(orders.contains(od)) return;
        orders.add(od);
        od.getOrder().addDrink(od);
    }

    public void removeOrder(Order order, boolean heated, boolean cooled, Drink_Size size, List<String> toppings) {
        for (Order_Drink o : orders) {
            if(o.equals(new Order_Drink(order, this, heated, cooled, size, toppings))){
                if(orders.contains(o)){
                    orders.remove(o);
                    order.removeDrink(o);
                    return;
                }
                return;
            }
        }
    }

    public void removeOrder(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        orders.remove(od);
        od.getOrder().removeDrink(od);
    }

    public void setOrder(Order_Drink od, Order_Drink nod) {
        if (od == null) throw new ValidationException("Invalid data");
        if (nod == null) throw new ValidationException("Invalid data");
        if(orders.contains(od)){
            orders.remove(od);
            orders.add(nod);
            od.getOrder().setDrink(od, nod);
        }
    }
}