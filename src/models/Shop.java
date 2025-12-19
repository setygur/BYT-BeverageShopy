package models;

import models.utils.OrderQualifier;
import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@JsonSerializable
public class Shop implements Validatable {
    @ObjectList
    public static List<Shop> shops = new ArrayList<>();

    @Derived
    private int salesNum;
    @NotNull
    @NotFuture
    private LocalDateTime dateOfLastStock;

    private Map<OrderQualifier, Order> orders = new HashMap<>();

    @JsonCtor
    public Shop(LocalDateTime dateOfLastStock) {
        this.dateOfLastStock = dateOfLastStock;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        this.salesNum = 0;

        shops.add(this);
    }

    public void addOrder(LocalDateTime time, Employee cashier, Order order) {
        if(time == null) throw new ValidationException("Invalid data");
        if(cashier == null) throw new ValidationException("Invalid data");
        if(order == null) throw new ValidationException("Invalid data");
        OrderQualifier key = OrderQualifier.find(time, cashier);
        if(key == null) {
            key = new OrderQualifier(time, cashier); //check time and cashier !null in ctor
        }
        if (orders.containsKey(key)) return;
        orders.put(key, order);
        order.addShop(this);
    }

    public void removeOrder(LocalDateTime time, Employee cashier, Order order) {
        if(time == null) throw new ValidationException("Invalid data");
        if(cashier == null) throw new ValidationException("Invalid data");
        if(order == null) throw new ValidationException("Invalid data");
        OrderQualifier key = OrderQualifier.find(time, cashier);
        if(key == null) throw new ValidationException("Invalid data");
        if(orders.containsKey(key)) {
            orders.remove(key);
            order.removeShop(this);
        }
    }

    public void setOrder(OrderQualifier oq, Order order, OrderQualifier oq2, Order order2) {
        if(oq == null) throw new ValidationException("Invalid data");
        if(oq2 == null) throw new ValidationException("Invalid data");
        if(order == null) throw new ValidationException("Invalid data");
        if(order2 == null) throw new ValidationException("Invalid data");
        if(orders.containsKey(oq)) {
            if(orders.get(oq).equals(order)) {
                orders.remove(oq);
                order.removeShop(this);
                orders.put(oq2, order2);
                order2.addShop(this);
            }
        }
    }

    public Order getOrder(LocalDateTime time, Employee cashier) {
        OrderQualifier key = new OrderQualifier(time, cashier);
        return orders.get(key);
    }

    public int getDaysFromLastStock() {
        if (dateOfLastStock == null) return 0;
        return (int) ChronoUnit.DAYS.between(dateOfLastStock.toLocalDate(), LocalDate.now());
    }
}