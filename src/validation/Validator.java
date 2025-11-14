package validation;

import persistence.ObjectList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Validator {
    //DO NOT RETAIN REFERENCES TO CLASSES
    public static boolean validate(Object o) throws ValidationException,  IllegalAccessException {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> eitherOrMap = new HashMap<>();
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
                            throw new ValidationException("Field " + field.getName() + " is required (null)");
                        }
                        String value = field.get(o).toString();
                        value = value.trim();
                        if (value.isEmpty()) {
                            throw new ValidationException("Field " + field.getName() + " is required (empty)");
                        }
                        break;
                    case "Unique":
                        for (Field field1 : fields) {
                            if(field1.isAnnotationPresent(ObjectList.class)){
                                List<Object> objectList = (List<Object>) field1.get(o);
                                for (Object o2 : objectList) {
                                    try{
                                        Field assertField = o2.getClass().getDeclaredField(field.getName());
                                        assertField.setAccessible(true);
                                        if (field.get(o).toString().trim().
                                                equalsIgnoreCase((assertField.get(o2).toString().trim()))){
                                            throw new ValidationException("Field " + field.getName() +
                                                    " must be unique");
                                        }
                                    } catch (NoSuchFieldException e) {
                                        throw new ValidationException(e.getMessage());
                                    }
                                }
                            }
                        }
                        break;
                    case "NotFuture":
                        if (field.get(o) == null) {
                            continue;
                        } else if (field.get(o) instanceof LocalDateTime) {
                            if(((LocalDateTime) field.get(o)).isAfter(LocalDateTime.now())) {
                                throw new ValidationException("Field " + field.getName() + " has to be before now");
                            }
                        } else if (field.get(o) instanceof LocalDate) {
                            if(((LocalDate) field.get(o)).isAfter(LocalDate.now())) {
                                throw new ValidationException("Field " + field.getName() + " has to be before now");
                            }
                        } else if (field.get(o) instanceof LocalTime) { //weird because it does not store date
                            // and might get freaky
                            if(((LocalTime) field.get(o)).isAfter(LocalTime.now())) {
                                throw new ValidationException("Field " + field.getName() + " has to be before now");
                            }
                        }
                        break;
                    case "Range":
                        if (field.get(o) == null) {
                            throw new ValidationException("Field " + field.getName() + " is required");
                        }
                        Type t = field.getGenericType();
                        Range range = field.getAnnotation(Range.class);

                        if(t.equals(int.class)) {
                            int v = field.getInt(o);
                            int min = (int) range.min();
                            int max = (int) range.max();
                            if (v < min || v > max) {
                                throw new ValidationException("Field " + field.getName() + " is out of range");
                            }
                        } else if (t.equals(double.class)) {
                            double v =  field.getDouble(o);
                            double min = range.min();
                            double max = range.max();
                            if (v < min || v > max) {
                                throw new ValidationException("Field " + field.getName() + " is out of range");
                            }
                        }else if (t.equals(long.class)) {
                            long v = field.getLong(o);
                            long min = (long) range.min();
                            long max = (long) range.max();
                            if (v < min || v > max) {
                                throw new ValidationException("Field " + field.getName() + " is out of range");
                            }
                        }
                        break;
                    case "Derived":
                        if (field.get(o) != null) {
                            throw new ValidationException("Field " + field.getName() +
                                    " is derived and should not be assigned");
                        }
                        break;
                    case "NotEmpty":

                        if (field.get(o) == null) {
                            throw new ValidationException("Field " + field.getName() + " must not be null or empty");
                        }

                        // Strings
                        if (field.get(o) instanceof String) {
                            String s = ((String) field.get(o)).trim();
                            if (s.isEmpty()) {
                                throw new ValidationException("Field " + field.getName() + " must not be empty");
                            }
                            break;
                        }

                        // Collections
                        if (field.get(o) instanceof java.util.Collection) {
                            if (((java.util.Collection<?>) field.get(o) ).isEmpty()) {
                                throw new ValidationException("Field " + field.getName() + " must not be empty");
                            }
                        } else if (field.get(o).getClass().isArray()) {
                            if (java.lang.reflect.Array.getLength(field.get(o)) == 0) {
                                throw new ValidationException("Field " + field.getName() + " must not be empty");
                            }
                        }
                        break;
                    case "Derived":
                        if(field.getType() == byte.class){
                            if((byte)field.get(o) != 0){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == short.class){
                            if((short)field.get(o) != 0){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == int.class){
                            if((int)field.get(o) != 0){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == long.class){
                            if((long)field.get(o) != 0){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == double.class){
                            if((double)field.get(o) != 0.0){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == float.class){
                            if((float)field.get(o) != 0.0){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == boolean.class){
                            if((boolean) field.get(o)){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else if(field.getType() == char.class){
                            if((char)field.get(o) != '\u0000'){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        } else {
                            if(field.getType() != null){
                                throw new ValidationException("Field " + field.getName() + " must be derived");
                            }
                        }
                        break;
//                    case "Range":
//                        Range range = field.getAnnotation(Range.class);
//
//                        if(!(field.get(o) instanceof Integer)){
//                            throw new ValidationException("Field " + field.getName() + " must be numeric");
//                        }
//
//                        int numericValue = ((Number) field.get(o)).intValue();
//                        if (numericValue < range.min() || numericValue > range.max()) {
//                            throw new ValidationException("Field " + field.getName() +
//                                    " must be between " + range.min() +
//                                    " and " + range.max());
//                        }
//                        break;
//                    default:
//                        System.err.println("Annotation ignored at validation: " +
//                                annotation.annotationType().getSimpleName());
                }
            }
        }
        return true;
    }
}
