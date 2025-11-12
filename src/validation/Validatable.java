package validation;

public interface Validatable {
    default boolean validate(Object o) {
        return Validator.validate(o);
    }
}
