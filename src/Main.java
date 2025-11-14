import models.Coffee;
import models.Employee;
import models.Fruit;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Employee e = new Employee("John", "Silverhand","john@arasaka.corp",
                "kkkkk", "PK99901");

        System.out.println(e.toString());

        Employee e2 = new Employee("John", "Silverhand","john@arasaka.corp",
                null, "PK99901");

        Coffee coffee = new Coffee(6);
        List<String> fruitzz = new ArrayList<>();
        Fruit smoothie = new Fruit(fruitzz, true);
    }
}
