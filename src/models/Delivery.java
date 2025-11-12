package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Delivery implements Validatable {
    @ObjectList
    public static List<Delivery> deliverys = new ArrayList<>();

    @NotBlank
    private String timeStarted;
    @NotBlank  // multiplicity [0..1] interpreted as optional String; drop @NotBlank if you prefer nullable
    private String timeDelivered;
    @NotNull
    private int capacityKg;
    @NotBlank
    private String status;

    public Delivery(String timeStarted, String timeDelivered, int capacityKg, String status) {
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