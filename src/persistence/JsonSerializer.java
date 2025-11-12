package persistence;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class JsonSerializer {
    //implement serializer and create factory if more than json persistence would exist

    //serialize one object to a json string
    public static String serialize(Object o) throws SerializeException {
        if (o == null) {
            throw new SerializeException("Object is null");
        }
        if (!o.getClass().isAnnotationPresent(JsonSerializable.class)) {
            throw new SerializeException("Object is not JsonSerializable");
        }
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }
            try{
                field.setAccessible(true);
                String fieldName = "\"" + field.getName() + "\"";
                String value = field.get(o).toString();
                if(field.getType() == String.class) {
                    value = "\"" + value + "\"";
                }
                sj.add(fieldName + ":" + value);
            } catch (IllegalAccessException e){
                throw new SerializeException(e.getMessage());
            }
        }
        return sj.toString();
    }

    //by passing an array of objects of the same class, we can create a json field with all the object of this class
    public static String serializeArray(Object[] oArr) throws SerializeException {
        String objectName = "";
        for(int i =  0; i < oArr.length; i++){
            if(oArr[i] == null){
                continue;
            } else if(oArr[i].getClass().isAnnotationPresent(JsonSerializable.class)) {
                objectName = oArr[i].getClass().getName();
                break;
            } else {
                throw  new SerializeException("Object is not JsonSerializable");
            }
        }
        StringJoiner sj = new StringJoiner(",", "\"" + objectName + "\":", "");
        for (Object o : oArr) {
            sj.add(serialize(o));
        }
        return sj.toString();
    }
}
