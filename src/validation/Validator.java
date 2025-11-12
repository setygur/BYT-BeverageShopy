package validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Validator {
    public static boolean validate(Object o) throws ValidationException, IllegalAccessException {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                switch (annotation.annotationType().getSimpleName()) {
                    case "NotNull":
                        if (field.get(o) == null) {
                            throw new ValidationException("Field " + field.getName() + " is required");
                        }
                        break;
                    case "NotBlank":
                        if (field.get(o) == null) {
                            throw new ValidationException("Field " + field.getName() + " is required");
                        }
                        String value = field.get(o).toString();
                        value = value.trim();
                        if (value.isEmpty()) {
                            throw new ValidationException("Field " + field.getName() + " is required");
                        }
                        break;
                    case "Unique":
                        //TODO add unique logic
                        break;
                    case "EitherOr":
                        //TODO add eitherOr logic
                        break;
                    case "Derived":
                        if (field.get(o) != null) {
                            throw new ValidationException("Field " + field.getName() +
                                    " is derived and should not be assigned");
                        }
                        break;
                    default:
                        System.out.println(annotation.annotationType().getSimpleName());
                }
            }
        }
        return true;
    }
}
