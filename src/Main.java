import models.Employee;

public class Main {

    public static void main(String[] args) {
        Employee e = new Employee("John", "Silverhand","john@arasaka.corp",
                "kkkkk", "PK99901");

        System.out.println(e.toString());

        Employee e2 = new Employee("John", "Silverhand","john@arasaka.corp",
                null, "PK99901");

        System.out.println(e2.toString());
    }
}
