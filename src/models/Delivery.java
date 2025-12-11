package models;

import models.utils.Status;
import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Delivery implements Validatable {

    @ObjectList
    public static List<Delivery> deliverys = new ArrayList<>();

    @NotBlank
    @NotFuture
    @NotNull
    private LocalDateTime timeStarted;

    private LocalDateTime timeDelivered;

    @NotNull
    private double capacityKg;

    @NotNull
    private Status status;

    // The warehouse this delivery was taken from (0..1)
    private Warehouse sourceWarehouse;

    @JsonCtor
    public Delivery(LocalDateTime timeStarted,
                    LocalDateTime timeDelivered,
                    int capacityKg,
                    Status status,
                    Warehouse sourceWarehouse) {

        this.timeStarted      = timeStarted;
        this.timeDelivered    = timeDelivered;
        this.capacityKg       = capacityKg;
        this.status           = status;
        this.sourceWarehouse  = sourceWarehouse;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }

        if (sourceWarehouse != null)
            sourceWarehouse.addDelivery(this);

        deliverys.add(this);
    }

    public Warehouse getSourceWarehouse() {
        return sourceWarehouse;
    }
}