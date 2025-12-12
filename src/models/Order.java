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
    private Cashier cashier;
    private Shop shop;

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

    public void addShop(Shop shop) {
        if (shop == null) throw new ValidationException("Invalid data");
        if (this.shop == shop) return;
        this.shop = shop;
        shop.addOrder(timeOfOrder, cashier, this);
    }

    public void removeShop(Shop shop) {
        if (shop == null) throw new ValidationException("Invalid data");
        if (this.shop == shop) {
            this.shop = null;
            shop.removeOrder(timeOfOrder, cashier, this);
        }
    }

    public void setShop(Shop oldShop, Shop newShop) {
        if (oldShop == null) throw new ValidationException("Invalid data");
        if (newShop == null) throw new ValidationException("Invalid data");
        if (this.shop == oldShop) {
            oldShop.removeOrder(timeOfOrder, cashier, this);
            this.shop = newShop;
            newShop.addOrder(timeOfOrder, cashier, this);
        }
    }

    public void addCashier(Cashier cashier) {
        if (cashier == null) throw new ValidationException("Invalid data");
        if (this.cashier == cashier) return;
        this.cashier = cashier;
        cashier.addOrder(this);
    }

    public void removeCashier(Cashier cashier) {
        if (cashier == null) throw new ValidationException("Invalid data");
        if (this.cashier == cashier) {
            this.cashier = null;
            cashier.removeOrder(this);
        }
    }

    public void setCashier(Cashier oldCashier,  Cashier newCashier) {
        if (oldCashier == null) throw new ValidationException("Invalid data");
        if (newCashier == null) throw new ValidationException("Invalid data");
        if (this.cashier == oldCashier) {
            oldCashier.removeOrder(this);
            this.cashier = newCashier;
            newCashier.addOrder(this);
        }
    }

    public double getTotalPrice() {
        // Mocked: sum of drink base cost + size cost + toppings cost
        double baseCost = 5.0;
        double sizeCost = 1.0;
        double toppingsCost = 2.0;
        return baseCost + sizeCost + toppingsCost + tip;
    }

    public void addDrink(Drink drink, boolean heated, boolean cooled, Drink_Size size, List<String> toppings) {
        if (drink == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        Order_Drink od = Order_Drink.find(this, drink, heated, cooled, size, toppings);
        if(od == null) {
            od = new Order_Drink(this, drink, heated, cooled, size, toppings);
        }
        drinks.add(od);
        drink.addOrder(od);
    }

    public void addDrink(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if(this.drinks.contains(od)) return;
        this.drinks.add(od);
        od.getDrink().addOrder(od);
    }

    public void removeDrink(Drink drink, boolean heated, boolean cooled, Drink_Size size, List<String> toppings) {
        for (Order_Drink o : drinks) {
            if(o.equals(new Order_Drink(this, drink, heated, cooled, size, toppings))){
                if(drinks.contains(o)){
                    drinks.remove(o);
                    drink.removeOrder(o);
                    return;
                }
                return;
            }
        }
    }

    public void removeDrink(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if(!this.drinks.contains(od)) return;
        drinks.remove(od);
        od.getDrink().removeOrder(od);
    }

    public void setDrink(Order_Drink od, Order_Drink nod) {
        if (od == null) throw new ValidationException("Invalid data");
        if (nod == null) throw new ValidationException("Invalid data");
        if(drinks.contains(od)){
            drinks.remove(od);
            drinks.add(nod);
            od.getDrink().setOrder(od, nod);
        }
    }
}