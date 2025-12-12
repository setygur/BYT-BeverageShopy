package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Loader extends Employee implements Validatable {
    private static final int DELIVERY_BONUS = 125;
    private static final int HOURLY_RATE = 25;
    @ObjectList
    public static List<Loader> loaders = new ArrayList<>();

    @NotNull
    @Range(min = 0)
    private double loaderEvaluationScore;
    @JsonIgnore
    @Derived
    @Range(min = 0)
    private double salary;
    private List<Delivery> deliveries = new ArrayList<>();

    @JsonCtor
    public Loader(String name, String surname, String email, String peselNumber,
                  String passportNumber, double loaderEvaluationScore) {
        super(name, surname, email, peselNumber, passportNumber);
        this.loaderEvaluationScore = loaderEvaluationScore;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salary = 0.0; // TODO derive after validation
        loaders.add(this);
    }

    public double getSalary() {
        // TODO get the following vars
        double totalHours = 0;
        double deliveries = 0;

        double salary = (totalHours * HOURLY_RATE) +
                (deliveries * DELIVERY_BONUS);

        return Math.max(0, salary);
    }

    public void addDelivery(Delivery delivery) {
        if (delivery == null) throw new ValidationException("Invalid data");
        if (this.getShifts().isEmpty()) throw new ValidationException("Not assigned to the shift");
        if(!deliveries.contains(delivery)){
            if(delivery.addLoader(this)){
                deliveries.add(delivery);
            }
        }
    }

    public void removeDelivery(Delivery delivery) {
        if (delivery == null) throw new ValidationException("Invalid data");
        if(!deliveries.contains(delivery)) return ;
        deliveries.remove(delivery);
        delivery.removeLoader(this);
    }

    public void setDelivery(Delivery oldDelivery,  Delivery newDelivery) {
        if (oldDelivery == null) throw new ValidationException("Invalid data");
        if (newDelivery == null) throw new ValidationException("Invalid data");
        if(deliveries.contains(oldDelivery)){
            deliveries.remove(oldDelivery);
            oldDelivery.removeLoader(this);
            deliveries.add(newDelivery);
            newDelivery.addLoader(this);
        }
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
}