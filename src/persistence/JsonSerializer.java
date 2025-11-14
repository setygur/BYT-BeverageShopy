package persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.StringJoiner;

public class JsonSerializer {
    //implement serializer and create factory if more than json persistence would exist

    //serialize one object to a json string
    private static String serialize(Object o) throws SerializeException {
        if (o == null) throw new SerializeException("Object is null");
        if (!o.getClass().isAnnotationPresent(JsonSerializable.class)) {
            throw new SerializeException("Object is not JsonSerializable");
        }

        StringJoiner sj = new StringJoiner(",\n", "{\n", "\n}");
        Class<?> c = o.getClass();
        while (c != Object.class) {
            for (Field field : c.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.isAnnotationPresent(ObjectList.class)) continue;
                if (field.isAnnotationPresent(JsonIgnore.class)) continue;

                field.setAccessible(true);
                try {
                    Object raw = field.get(o);
                    String name = "\"" + field.getName() + "\"";
                    String value = (raw == null) ? "null"
                            : (field.getType() == String.class ? "\"" + escapeJson(raw.toString()) + "\"" : raw.toString());
                    sj.add(name + ":" + value);
                } catch (IllegalAccessException e) {
                    throw new SerializeException(e.getMessage());
                }
            }
            c = c.getSuperclass();
        }
        return sj.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b","\\b")
                .replace("\f","\\f")
                .replace("\n","\\n")
                .replace("\r","\\r")
                .replace("\t","\\t");
    }

    private static String serializeStaticFields(Class<?> clazz) throws SerializeException {
        StringJoiner sj = new StringJoiner(",\n", "{\n", "\n}");
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue;
                if (field.isAnnotationPresent(ObjectList.class)) continue; // don’t dump the registry list
                if (field.isAnnotationPresent(JsonIgnore.class)) continue;

                field.setAccessible(true);
                Object raw = field.get(null);
                String name = "\"" + field.getName() + "\"";
                String value;
                if (raw == null) {
                    value = "null";
                } else if (field.getType() == String.class) {
                    value = "\"" + escapeJson(raw.toString()) + "\"";
                } else {
                    value = raw.toString();
                }
                sj.add(name + ":" + value);
            }
        } catch (IllegalAccessException e) {
            throw new SerializeException("Static field access failed in " + clazz.getName() + ": " + e.getMessage());
        }
        return sj.toString();
    }

    //by passing an array of objects of the same class, we can create a json field with all the object of this class
    private static String serializeArray(Class<?> clazz, Object[] oArr) throws SerializeException {
        // objects
        StringJoiner objects = new StringJoiner(",\n", "[\n", "\n]");
        for (Object o : oArr) {
            if (o == null) continue;
            objects.add(serialize(o));
        }

        String staticJson = serializeStaticFields(clazz);

        String key = "\"" + clazz.getName() + "\"";
        return key + ":{\n" +
                "\"static\":" + staticJson + ",\n" +
                "\"objects\":" + objects.toString() + "\n" +
                "}";
    }

    public static String serializeObjects(List<Class<?>> classes) throws SerializeException {
        StringJoiner sj = new StringJoiner(",\n", "{\n\"models\":{\n", "\n}\n}");
        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(ObjectList.class)) continue;

                try {
                    field.setAccessible(true);
                    Object value = field.get(null); // static list holder
                    if (value instanceof List<?> list) {
                        if (list.isEmpty()) continue; // skip empties
                        Object[] array = list.toArray();
                        sj.add(serializeArray(clazz, array));
                    }
                } catch (IllegalAccessException e) {
                    throw new SerializeException("Could not access field " +
                            field.getName() + " in " + clazz.getName() + ": " + e.getMessage());
                }
            }
        }
        return sj.toString();
    }
}
