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
    private LocalDateTime timeStarted;
    @NotBlank  // multiplicity [0..1] interpreted as optional String; drop @NotBlank if you prefer nullable
    private LocalDateTime timeDelivered;
    @NotNull
    private double capacityKg;
    @NotNull
    private Status status;

    @JsonCtor
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
    }
}