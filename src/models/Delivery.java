package models;

import models.utils.Status;
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
    private LocalDateTime timeStarted;
    private LocalDateTime timeDelivered; //may be null
    @NotNull
    private double capacityKg;
    @NotNull
    private Status status;

    public Delivery(LocalDateTime timeStarted, LocalDateTime timeDelivered, int capacityKg, Status status) {
        this.timeStarted = timeStarted;
        this.timeDelivered = timeDelivered;
        this.capacityKg = capacityKg;
        this.status = status;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        deliverys.add(this);
    }
}