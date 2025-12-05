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

    //TODO write unit tests
    public void addOrder(LocalDateTime time, Cashier cashier, Order order) {
        OrderQualifier key = new OrderQualifier(time, cashier);
        if (orders.containsKey(key)) {
            throw new IllegalArgumentException(
                    "An order with the same TimeOfOrder and Cashier already exists."
            );
        }
        orders.put(key, order);
    }

    public Order getOrder(LocalDateTime time, Cashier cashier) {
        OrderQualifier key = new OrderQualifier(time, cashier);
        return orders.get(key);
    }

    public int getDaysFromLastStock() {
        if (dateOfLastStock == null) return 0;
        return (int) ChronoUnit.DAYS.between(dateOfLastStock.toLocalDate(), LocalDate.now());
    }
}