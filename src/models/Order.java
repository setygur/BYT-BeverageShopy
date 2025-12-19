package models;

import models.aspects.SweetenerAspect;
import models.aspects.TemperatureAspect;
import models.utils.Drink_Size;
import persistence.JsonCtor;
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

    private final List<Order_Drink> drinks = new ArrayList<>();

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

    // ---------------- Associations: Shop ----------------

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

    // ---------------- Associations: Cashier ----------------

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

    public void setCashier(Cashier oldCashier, Cashier newCashier) {
        if (oldCashier == null) throw new ValidationException("Invalid data");
        if (newCashier == null) throw new ValidationException("Invalid data");
        if (this.cashier == oldCashier) {
            oldCashier.removeOrder(this);
            this.cashier = newCashier;
            newCashier.addOrder(this);
        }
    }

    // ---------------- Drinks (Association Class) ----------------

    public void addDrink(
            Drink drink,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        if (drink == null) throw new ValidationException("Invalid data");
        if (temperature == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");

        Set<SweetenerAspect> safeSweeteners =
                (sweeteners == null) ? Collections.emptySet() : new HashSet<>(sweeteners);

        List<String> safeToppings = new ArrayList<>(toppings);

        Order_Drink od = Order_Drink.find(this, drink, temperature, safeSweeteners, size, safeToppings);
        if (od == null) {
            od = new Order_Drink(this, drink, temperature, safeSweeteners, size, safeToppings);
        }

        addDrink(od); // keep inverse consistent
    }

    public void addDrink(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if (od.getOrder() != this) throw new ValidationException("Order_Drink belongs to a different Order");
        if (this.drinks.contains(od)) return;

        this.drinks.add(od);
        od.getDrink().addOrder(od); // sync inverse
    }

    public void removeDrink(
            Drink drink,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        if (drink == null) throw new ValidationException("Invalid data");
        if (temperature == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");

        Set<SweetenerAspect> safeSweeteners =
                (sweeteners == null) ? Collections.emptySet() : new HashSet<>(sweeteners);

        Order_Drink od = Order_Drink.find(this, drink, temperature, safeSweeteners, size, toppings);
        if (od != null) {
            removeDrink(od);
        }
    }

    public void removeDrink(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if (!this.drinks.contains(od)) return;

        this.drinks.remove(od);
        od.getDrink().removeOrder(od); // sync inverse
    }

    public void setDrink(Order_Drink oldOd, Order_Drink newOd) {
        if (oldOd == null) throw new ValidationException("Invalid data");
        if (newOd == null) throw new ValidationException("Invalid data");
        if (!this.drinks.contains(oldOd)) return;
        if (newOd.getOrder() != this) throw new ValidationException("New Order_Drink belongs to a different Order");

        this.drinks.remove(oldOd);
        this.drinks.add(newOd);

        oldOd.getDrink().setOrder(oldOd, newOd); // sync inverse
    }

    // ---------------- Total price ----------------

    public double getTotalPrice() {
        double sum = 0.0;
        for (Order_Drink od : drinks) {
            sum += od.getAdditionalCost(); // uses derived cost from Order_Drink
        }
        return sum + tip;
    }

    // ---------------- getters (needed by Order_Drink.find) ----------------

    public long getOrderId() { return orderId; }
    public LocalDateTime getTimeOfOrder() { return timeOfOrder; }
    public double getTip() { return tip; }
    public List<Order_Drink> getDrinks() { return Collections.unmodifiableList(drinks); }
}