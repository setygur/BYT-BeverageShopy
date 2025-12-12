package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Cashier extends Employee {
    @ObjectList
    public static List<Cashier> cashiers = new ArrayList<>();

    @NotNull
    private boolean handlesCash;
    @NotBlank
    @Unique
    private String cashierId;
    @NotNull
    private double cashierEvaluationScore;
    private List<Order> orders = new ArrayList<>();

    @JsonCtor
    public Cashier(String name, String surname, String email, String peselNumber, String passportNumber, boolean handlesCash, String cashierId, double cashierEvaluationScore) {
        super(name, surname, email, peselNumber, passportNumber);
        this.handlesCash = handlesCash;
        this.cashierId = cashierId;
        this.cashierEvaluationScore = cashierEvaluationScore;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        cashiers.add(this);
    }

    public void addOrder(Order order) {
        if (order ==  null) throw new ValidationException("Invalid data");
        if (orders.contains(order)) return;
        orders.add(order);
        order.addCashier(this);
    }

    public void removeOrder(Order order) {
        if (order ==  null) throw new ValidationException("Invalid data");
        if (orders.contains(order)) {
            orders.remove(order);
            order.removeCashier(this);
        };
    }

    public void setOrder(Order oldOrder, Order order) {
        if (oldOrder ==  null) throw new ValidationException("Invalid data");
        if (order == null) throw new ValidationException("Invalid data");
        if (orders.contains(oldOrder)) {
            orders.remove(oldOrder);
            oldOrder.removeCashier(this);
            orders.add(order);
            order.addCashier(this);
        }
    }

    public double getHoursOnShift() {
        // Mocked example value
        return 40.0;
    }

    public double getTips() {
        // Mocked example value for frequent customer registered tips
        return 150.0;
    }

    public double getSalary() {
        // salary = hoursOnShift * rate + tips
        double hourlyRate = 20.0; // mocked hourly rate
        return (getHoursOnShift() * hourlyRate) + getTips();
    }
}