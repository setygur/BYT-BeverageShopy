package models;

import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Shift implements Validatable {
    @ObjectList
    public static List<Shift> shifts = new ArrayList<>();

    @Derived
    private double duration;
    @NotNull
    private LocalDateTime beginningTime;
    @NotNull
    private LocalDateTime endTime;

    public Shift(LocalDateTime beginningTime, LocalDateTime endTime) {
        this.beginningTime = beginningTime;
        this.endTime = endTime;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        // TODO: make a derived logic
        this.duration = 0.0;
    }
}