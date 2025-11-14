package tests.models;

import models.Coffee;
import models.Delivery;
import models.Drink;
import models.Employee;
import models.Facility;
import models.Fruit;
import models.Order;
import models.Order_Drink;
import models.Warehouse;
import models.utils.Address;
import models.utils.Drink_Size;
import models.utils.Status;
import models.utils.TypeOfMilk;
import models.utils.TypeOfTea;
import persistence.ObjectList;
import validation.ValidationException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple built-in assertion based suite (run with: java -ea tests.models.ModelsSmokeTest).
 * Each test relies only on core Java assertions and throws no external dependency.
 */
public class ModelsSmokeTest {

    private int passed;
    private int failed;

    public static void main(String[] args) {
        ModelsSmokeTest suite = new ModelsSmokeTest();
        suite.runAll();
        System.out.println("Tests passed: " + suite.passed);
        System.out.println("Tests failed: " + suite.failed);
        if (suite.failed > 0) {
            System.exit(1);
        }
    }

    private void runAll() {
        run("employeeCreatesWithPassport", this::employeeCreatesWithPassport);
        run("employeeRequiresId", this::employeeRequiresId);
        run("drinkRequiresNonNegativePrice", this::drinkRequiresNonNegativePrice);
        run("coffeeWithinRangeCreates", this::coffeeWithinRangeCreates);
        run("fruitRequiresItems", this::fruitRequiresItems);
        run("facilityRequiresAddress", this::facilityRequiresAddress);
        run("warehouseCapacityCannotBeNegative", this::warehouseCapacityCannotBeNegative);
        run("orderRequiresTimestamp", this::orderRequiresTimestamp);
        run("deliveryRequiresStatus", this::deliveryRequiresStatus);
        run("orderDrinkRequiresSize", this::orderDrinkRequiresSize);
    }

    private void run(String testName, Runnable test) {
        resetObjectLists();
        try {
            test.run();
            passed++;
        } catch (AssertionError | RuntimeException e) {
            failed++;
            System.err.println("Test '" + testName + "' failed: " + e.getMessage());
        }
    }

    private void employeeCreatesWithPassport() {
        Employee employee = new Employee("Alex", "Smith", "alex@corp.com", null, "PASS-001");
        assert employee != null : "Employee should be created when passport present";
        assert Employee.employees.size() == 1 : "Employee list should contain the new employee";
    }

    private void employeeRequiresId() {
        expectValidation("Employee must have at least one document",
                () -> new Employee("Alex", "Smith", "alex2@corp.com", null, null));
    }

    private void drinkRequiresNonNegativePrice() {
        expectValidation("Drink base price must be >= 0", () -> new Drink(
                "Sunrise",
                -1.0,
                "nuts",
                new Coffee(5),
                new models.Tea(TypeOfTea.GREEN),
                new models.Milk(TypeOfMilk.OAT),
                new Fruit(Collections.singletonList("Apple"), true)
        ));
    }

    private void coffeeWithinRangeCreates() {
        Coffee coffee = new Coffee(5);
        assert coffee != null : "Coffee should be created with caffeine within range";
    }

    private void fruitRequiresItems() {
        expectValidation("Fruit list cannot be empty",
                () -> new Fruit(Collections.emptyList(), true));
    }

    private void facilityRequiresAddress() {
        Facility facility = new Facility(new Address("Night City", "Afterlife Ave", "42B", 12345));
        assert facility != null : "Facility should be created when address provided";
        expectValidation("Facility requires address", () -> new Facility(null));
    }

    private void warehouseCapacityCannotBeNegative() {
        expectValidation("Warehouse capacity must be non-negative", () -> new Warehouse(-5, true));
    }

    private void orderRequiresTimestamp() {
        expectValidation("Order timestamp required", () -> new Order(10L, null, 0));
    }

    private void deliveryRequiresStatus() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        Delivery delivery = new Delivery(start, start.plusHours(2), 25, Status.ENROUTE);
        assert delivery != null : "Delivery should be created when status provided";
        expectValidation("Delivery requires status",
                () -> new Delivery(start, start.plusHours(2), 25, null));
    }

    private void orderDrinkRequiresSize() {
        expectValidation("Order drink requires size", () -> new Order_Drink(true, false, null));
        Order_Drink orderDrink = new Order_Drink(true, false, Drink_Size.MEDIUM);
        assert orderDrink != null : "Order drink should be created when size provided";
    }

    private void expectValidation(String message, Runnable action) {
        boolean thrown = false;
        try {
            action.run();
        } catch (ValidationException ex) {
            thrown = true;
        }
        assert thrown : message;
    }

    private void resetObjectLists() {
        List<Class<?>> modelTypes = Arrays.asList(
                Employee.class,
                Drink.class,
                models.Tea.class,
                models.Milk.class,
                Coffee.class,
                Fruit.class,
                Facility.class,
                Warehouse.class,
                Order.class,
                Delivery.class,
                Order_Drink.class
        );

        for (Class<?> type : modelTypes) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(ObjectList.class) && List.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        List<?> list = (List<?>) field.get(null);
                        if (list != null) {
                            list.clear();
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Unable to reset @ObjectList field on " + type.getSimpleName(), e);
                    }
                }
            }
        }
    }
}

