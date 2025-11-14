package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@JsonSerializable
public class Certification implements Validatable {
    @ObjectList
    public static List<Certification> certifications = new ArrayList<>();

    @NotBlank
    @Unique
    private String certificationId;
    @NotBlank
    private String certificationName;
    @NotNull
    @NotFuture
    private LocalDateTime timeOfCompletion;

    @JsonCtor
    public Certification(String certificationId, String certificationName, LocalDateTime timeOfCompletion) {
        this.certificationId = certificationId;
        this.certificationName = certificationName;
        this.timeOfCompletion = timeOfCompletion;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}