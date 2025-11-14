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

    @JsonIgnore
    @Derived
    private double duration;
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
        LocalTime start = LocalTime.parse(beginningTime.toString());
        LocalTime end = LocalTime.parse(endTime.toString());

        this.duration = (int) Duration.between(start, end).toMinutes();

        if (this.duration < 0) {
            this.duration += 24 * 60;
        }

        shifts.add(this);
    }
}