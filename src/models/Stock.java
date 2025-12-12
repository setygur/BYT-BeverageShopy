package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Stock implements Validatable {

    @ObjectList
    public static List<Stock> stocks = new ArrayList<>();

    @NotNull
    private LocalDateTime lastUpdated;

    @JsonIgnore
    @Derived
    private double salePrice;

    // 0..* history log of deliveries
    private List<Delivery> deliveryHistory = new ArrayList<>();

    // back-reference for 1..1 Facility–Stock composition
    private Facility facility;

    @JsonCtor
    public Stock(LocalDateTime lastUpdated, Facility facility) {
        this.lastUpdated = lastUpdated;
        this.facility = facility;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }

        this.salePrice = 0.0; // derive later if needed
        stocks.add(this);
    }

    public void recordDelivery(Delivery d) {
        if (d != null && !deliveryHistory.contains(d)) {
            deliveryHistory.add(d);
            lastUpdated = LocalDateTime.now();
        }
    }

    public List<Delivery> getDeliveryHistory() {
        return deliveryHistory;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public void setDeliveryHistory(List<Delivery> deliveryHistory) {
        this.deliveryHistory = deliveryHistory;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
