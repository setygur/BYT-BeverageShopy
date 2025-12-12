package models;

import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class Certification implements Validatable {
    @ObjectList
    public static List<Certification> certifications = new ArrayList<>();

    @NotNull
    private Employee employee; // The owner

    @NotBlank
    @Unique
    private String certificationId;
    @NotBlank
    private String certificationName;
    @NotNull
    @NotFuture
    private LocalDateTime timeOfCompletion;

    @JsonCtor
    public Certification(Employee employee, String certificationId, String certificationName, LocalDateTime timeOfCompletion) {
        if (employee == null) {
            throw new ValidationException("Certification cannot exist without an Employee (Composition)");
        }

        this.employee = employee;
        this.certificationId = certificationId;
        this.certificationName = certificationName;
        this.timeOfCompletion = timeOfCompletion;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        this.employee.internalAddCertification(this);
        certifications.add(this);
    }

    public void removeConnection() {
        if (this.employee != null) {
            certifications.remove(this);
            this.employee = null;
        }
    }

    public Employee getEmployee() {
        return employee;
    }
}