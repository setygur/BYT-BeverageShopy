package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class FrequentCustomer extends Person {
    @ObjectList
    public static List<FrequentCustomer> frequentCustomers = new ArrayList<>();
    @NotBlank
    private String phoneNumber;
    private static double baseDiscount;
    @NotNull
    private int amountOfOrders;
    private List<FrequentCustomer> referredCustomers = new ArrayList<>();
    private FrequentCustomer referrer = null;

    @JsonCtor
    public FrequentCustomer(String name, String surname, String email, String phoneNumber,  int amountOfOrders) {
        super(name, surname, email);
        this.phoneNumber = phoneNumber;
        this.amountOfOrders = amountOfOrders;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        frequentCustomers.add(this);
    }

    public int getAmountOfOrders() {
        // Mocked value for demonstration
        return amountOfOrders;
    }

    public double getCalculatedDiscount() {
        double refBonus = 0.0;
        for (FrequentCustomer ref : referredCustomers) {
            refBonus += ref.amountOfOrders / 2.0;
        }
        return amountOfOrders + refBonus;
    }

    public void addReferredCustomer(FrequentCustomer ref) {
        if (ref == null) throw new ValidationException("Invalid data");
        if (referredCustomers.contains(ref)) return;
        for (FrequentCustomer fc : frequentCustomers) {
            if(fc.referredCustomers.contains(ref)) throw new ValidationException("Customer already referred");
        }
        referredCustomers.add(ref);
        ref.addReferrer(this);
    }

    public void addReferrer(FrequentCustomer ref) {
        if (ref == null) throw new ValidationException("Invalid data");
        if (referrer != null) throw new ValidationException("Customer already referred");
        if (referrer == ref) return;
        referrer = ref;
        ref.addReferredCustomer(this);
    }

    public void removeReferredCustomer(FrequentCustomer ref) {
        if (ref == null) throw new ValidationException("Invalid data");
        if (referredCustomers.contains(ref)) {
            referredCustomers.remove(ref);
            ref.removeReferrer(this);
        }
    }

    public void removeReferrer(FrequentCustomer ref) {
        if (ref == null) throw new ValidationException("Invalid data");
        if(referrer == ref){
            referrer = null;
            ref.removeReferredCustomer(this);
        }
    }

    public void setReferredCustomer(FrequentCustomer ref, FrequentCustomer referred) {
        if (ref == null) throw new ValidationException("Invalid data");
        if (referred == null) throw new ValidationException("Invalid data");
        if (referredCustomers.contains(ref)) {
            referredCustomers.remove(ref);
            ref.removeReferrer(this);
            referredCustomers.add(referred);
            ref.addReferrer(this);
        }
    }

    public void setReferrer(FrequentCustomer ref, FrequentCustomer referred) {
        if (ref == null) throw new ValidationException("Invalid data");
        if (referred == null) throw new ValidationException("Invalid data");
        if (referrer == ref) {
            ref.removeReferredCustomer(this);
            referrer = referred;
            referrer.addReferredCustomer(this);
        }
    }
}
