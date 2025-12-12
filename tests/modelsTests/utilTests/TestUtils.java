package modelsTests.utilTests;


import java.lang.reflect.Field;
import java.util.List;

public final class TestUtils {
    private TestUtils() {}

    public static void resetObjectLists(Class<?>... classes) {
        for (Class<?> c : classes) {
            for (Field f : c.getDeclaredFields()) {
                try {
                    if (f.isAnnotationPresent(persistence.ObjectList.class)
                            && List.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        List<?> list = (List<?>) f.get(null);
                        if (list != null) list.clear();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Object target, String fieldName, Class<T> type) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object value = f.get(target);
            return (T) value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double getDoubleField(Object target, String fieldName) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.getDouble(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}