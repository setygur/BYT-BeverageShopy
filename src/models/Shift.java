package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@JsonSerializable
public class Shift implements Validatable {
    @ObjectList
    public static List<Shift> shifts = new ArrayList<>();

    @NotNull
    private LocalDateTime beginningTime;
    @NotNull
    private LocalDateTime endTime;

    @JsonCtor
    public Shift(LocalDateTime beginningTime, LocalDateTime endTime) {
        this.beginningTime = beginningTime;
        this.endTime = endTime;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        shifts.add(this);
    }

    public double getDuration() {
        if (beginningTime == null || endTime == null) return 0.0;
        double hours = Duration.between(beginningTime, endTime).toHours();
        if (hours < 0) {
            throw new ValidationException("Invalid duration of the shift. Must be a positive number");
        }
        return hours;
    }
}