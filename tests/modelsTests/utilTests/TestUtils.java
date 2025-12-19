package modelsTests.utilTests;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public final class TestUtils {
    private TestUtils() {}

    /**
     * Clears static lists annotated with @ObjectList.
     * SAFELY ignores instance fields to prevent crashes.
     */
    public static void resetObjectLists(Class<?>... classes) {
        for (Class<?> c : classes) {
            for (Field f : c.getDeclaredFields()) {
                try {
                    // CRITICAL FIX: Check if field is STATIC before calling get(null)
                    if (f.isAnnotationPresent(persistence.ObjectList.class)
                            && List.class.isAssignableFrom(f.getType())
                            && Modifier.isStatic(f.getModifiers())) { // <--- Added Check

                        f.setAccessible(true);
                        List<?> list = (List<?>) f.get(null);
                        if (list != null) list.clear();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to reset list: " + f.getName(), e);
                }
            }
        }
    }

    /**
     * Gets a field value, searching through the class hierarchy (Child -> Parent -> Object).
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(Object target, String fieldName, Class<T> type) {
        Class<?> clazz = target.getClass();

        // Walk up the hierarchy to find the field (e.g., finding 'name' in Person class)
        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return (T) f.get(target);
            } catch (NoSuchFieldException e) {
                // Not in this class, try superclass
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException("Error getting field " + fieldName, e);
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

    /**
     * Gets a double field value, searching through the class hierarchy.
     */
    public static double getDoubleField(Object target, String fieldName) {
        Class<?> clazz = target.getClass();

        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.getDouble(target);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException("Error getting double field " + fieldName, e);
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }
}