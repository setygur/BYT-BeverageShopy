package models.utils;

import models.Cashier;
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
    private Cashier cashier;

    @JsonCtor
    public OrderQualifier(LocalDateTime timeOfOrder, Cashier cashier) {
        this.timeOfOrder = timeOfOrder;
        this.cashier = cashier;
        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderQualifier)) return false;
        OrderQualifier that = (OrderQualifier) o;
        return Objects.equals(timeOfOrder, that.timeOfOrder) &&
                Objects.equals(cashier, that.cashier);
    }

    public Cashier getCashier() {
        return cashier;
    }

    public LocalDateTime getTimeOfOrder() {
        return timeOfOrder;
    }
}
