package validation;

import persistence.ObjectList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            throw new ValidationException("Field " + field.getName() + " is required");
                        }
                        String value = field.get(o).toString();
                        value = value.trim();
                        if (value.isEmpty()) {
                            throw new ValidationException("Field " + field.getName() + " is required");
                        }
                        break;
                    case "Unique":
                        var unique = field.get(o);
                        for (Field field1 : fields) {
                            if(field1.isAnnotationPresent(ObjectList.class)){
                                List<Object> objectList = (List<Object>) field1.get(o);
                                if (objectList.size() != 1) {
                                    for (Object o2 : objectList) {
                                        try{
                                            o2.getClass().getField(field.getName());
                                            if (field.get(o).toString().trim().
                                                    equalsIgnoreCase((field1.get(o2).toString().trim()))){
                                                throw new ValidationException("Field " + field.getName() +
                                                        " must be unique");
                                            }
                                        } catch (NoSuchFieldException e) {
                                            throw new ValidationException(e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "EitherOr":
                        System.out.println("EitherOr");
                        if(field.get(o) == null) {
                            System.out.println("Field " + field.getName());
//                            System.out.println(field.get(o).toString());
                            EitherOr eitherOr = field.getAnnotation(EitherOr.class);
                            try{
                                assert eitherOr != null;
                                Field field1 = clazz.getDeclaredField(eitherOr.dependsOn());
                                System.out.println("Field1 " +  field1.getName());
                                field1.setAccessible(true);
//                                if(field1.get(o) != null) {
//                                    System.out.println(field1.getName() + " is null");
//                                }else{
//                                    System.out.println(field1.get(o).toString());
//                                }
                                if(field1.get(o) == null){
                                    throw new ValidationException("Field " + field.getName() + " or " +
                                            field1.getName() + " is required");
                                }
                            } catch (NoSuchFieldException e) {
                                throw new ValidationException(e.getMessage());
                            }
                        }

                        break;
                    case "Derived":
                        if (field.get(o) != null) {
                            throw new ValidationException("Field " + field.getName() +
                                    " is derived and should not be assigned");
                        }
                        break;
                    default:
                        System.err.println("Annotation ignored at validation: " +
                                annotation.annotationType().getSimpleName());
                }
            }
        }
        return true;
    }
}
