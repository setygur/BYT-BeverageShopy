package persistence;

import java.util.List;

public class ModelClassesList {
    //TODO add the serializable classes to this List OR
    // write a reflective method that goes through all the .java in the .models package and add them to this list
    static List<Class<?>> modelClasses = List.of(models.Employee.class, models.FrequentCustomer.class);
}
