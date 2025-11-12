package validation;

public interface Validatable {
    default boolean validate(Object o) throws IllegalAccessException {
        return Validator.validate(o);
    }
}
