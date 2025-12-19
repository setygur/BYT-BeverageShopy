package models.utils;

import models.Employee;
import persistence.JsonCtor;
import persistence.ObjectList;
import validation.NotNull;
import validation.Validatable;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderQualifier implements Validatable {
    @ObjectList
    public static List<OrderQualifier> orderQualifiers = new ArrayList<>();

    @NotNull
    private LocalDateTime timeOfOrder;
    @NotNull
    private Employee cashier;

    @JsonCtor
    public OrderQualifier(LocalDateTime timeOfOrder, Employee cashier) {
        this.timeOfOrder = timeOfOrder;
        if(cashier.getType() != EmployeeType.CASHIER){
            throw new ValidationException("cashier type must be CASHIER");
        }
        this.cashier = cashier;
        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        orderQualifiers.add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderQualifier)) return false;
        OrderQualifier that = (OrderQualifier) o;
        return Objects.equals(timeOfOrder, that.timeOfOrder) &&
                Objects.equals(cashier, that.cashier);
    }

    public static OrderQualifier find(LocalDateTime time, Employee cashier) {
        for (OrderQualifier orderQualifier : orderQualifiers) {
            if(orderQualifier.timeOfOrder.equals(time) &&  orderQualifier.cashier.equals(cashier)) {
                return orderQualifier;
            }
        }
        return null;
    }

    public Employee getCashier() {
        return cashier;
    }

    public LocalDateTime getTimeOfOrder() {
        return timeOfOrder;
    }
}
