package persistence;

import java.util.List;

public class ModelClassesList {
    //TODO add the serializable classes to this List OR
    // write a reflective method that goes through all the .java in the .models package and add them to this list
    static List<Class<?>> modelClasses = List.of(
            models.Employee.class,
            models.FrequentCustomer.class,
            //models.Cashier.class,
            models.Certification.class,
//            models.Coffee.class,
            models.Delivery.class,
            models.Drink.class,
            models.Facility.class,
//            models.Fruit.class,
            //models.Loader.class,
            //models.Manager.class,
//            models.Milk.class,
            models.Order.class,
            models.Order_Drink.class,
            models.Person.class,
            //models.Quantity.class,
            models.Shift.class,
            models.Shop.class,
            models.Stock.class,
//            models.Tea.class,
            models.Warehouse.class
            );
}
