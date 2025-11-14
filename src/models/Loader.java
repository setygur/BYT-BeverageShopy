package models;

import persistence.JsonCtor;
import persistence.JsonIgnore;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;
import java.util.*;

@JsonSerializable
public class Loader implements Validatable {
    @ObjectList
    public static List<Loader> loaders = new ArrayList<>();

    @NotNull
    @Range(min = 0)
    private double loaderEvaluationScore;
    @JsonIgnore
    @Derived
    @Range(min = 0)
    private double salary;

    @JsonCtor
    public Loader(double loaderEvaluationScore) {
        this.loaderEvaluationScore = loaderEvaluationScore;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        this.salary = 0.0; // TODO derive after validation
        loaders.add(this);
    }
}