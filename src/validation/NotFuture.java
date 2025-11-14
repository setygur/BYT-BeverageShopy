package validation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFuture {
    String message() default "Date/time must not be in the future";
}