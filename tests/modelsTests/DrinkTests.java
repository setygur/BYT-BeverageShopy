package modelsTests;

import models.*;
import models.aspects.*;
import models.utils.Drink_Size;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DrinkTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(
                Drink.class, Order.class, Order_Drink.class,
                Coffee.class, Tea.class, Milk.class, Fruit.class
        );
    }

    // ---------- constructor ----------

    @Test
    void constructor_addsToStaticList_whenValid() {
        int before = Drink.drinks.size();

        Drink d = new Drink("Sunrise", 10.0, "nuts");

        assertNotNull(d);
        assertEquals(before + 1, Drink.drinks.size());
        assertTrue(Drink.drinks.contains(d));
    }

    @Test
    void throws_whenNameBlank() {
        assertThrows(ValidationException.class, () ->
                new Drink("   ", 10.0, "none")
        );
    }

    @Test
    void throws_whenAllergensBlank() {
        assertThrows(ValidationException.class, () ->
                new Drink("Latte", 10.0, "   ")
        );
    }

    @Test
    void throws_whenPriceNegative() {
        assertThrows(ValidationException.class, () ->
                new Drink(
                        "Sunrise",
                        -1.0,
                        "nuts")
        );
    }

    // ---------- addOrder (association class) ----------

    @Test
    void addOrder_throws_whenOrderNull() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () ->
                d.addOrder(
                        null,
                        new HotDrink(),
                        Set.of(),
                        Drink_Size.SMALL,
                        new ArrayList<>()
                )
        );
    }

    @Test
    void addOrder_throws_whenSizeNull() {
        Drink d = new Drink("D", 5.0, "none");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () ->
                d.addOrder(
                        o,
                        new HotDrink(),
                        Set.of(),
                        null,
                        new ArrayList<>()
                )
        );
    }

    @Test
    void addOrder_throws_whenToppingsNull() {
        Drink d = new Drink("D", 5.0, "none");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        assertThrows(ValidationException.class, () ->
                d.addOrder(
                        o,
                        new HotDrink(),
                        Set.of(),
                        Drink_Size.SMALL,
                        null
                )
        );
    }

    @Test
    void addOrder_createsOrderDrink_andBackLinks() {
        Drink d = new Drink("D", 5.0, "none");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        d.addOrder(
                o,
                new ColdDrink(),
                Set.of(new HoneySweetened()),
                Drink_Size.MEDIUM,
                List.of("x")
        );

        List<Order_Drink> drinkOrders =
                TestUtils.getField(d, "orders", List.class);
        List<Order_Drink> orderDrinks =
                TestUtils.getField(o, "drinks", List.class);

        assertEquals(1, drinkOrders.size());
        assertSame(drinkOrders.get(0), orderDrinks.get(0));
    }

    // ---------- removeOrder ----------

    @Test
    void removeOrder_OrderDrinkOverload_removesAndBackUnlinks() {
        Drink d = new Drink("D", 5.0, "none");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink od = new Order_Drink(
                o,
                d,
                new HotDrink(),
                Set.of(),
                Drink_Size.BIG,
                new ArrayList<>()
        );

        d.addOrder(od);
        d.removeOrder(od);

        List<Order_Drink> drinkOrders =
                TestUtils.getField(d, "orders", List.class);
        List<Order_Drink> orderDrinks =
                TestUtils.getField(o, "drinks", List.class);

        assertFalse(drinkOrders.contains(od));
        assertFalse(orderDrinks.contains(od));
    }

    // ---------- setOrder ----------

    @Test
    void setOrder_replacesAssociation_whenOldExists() {
        Drink d = new Drink("D", 5.0, "none");
        Order o = new Order(1L, LocalDateTime.now(), 0.0);

        Order_Drink oldOd = new Order_Drink(
                o,
                d,
                new HotDrink(),
                Set.of(),
                Drink_Size.SMALL,
                new ArrayList<>()
        );

        Order_Drink newOd = new Order_Drink(
                o,
                d,
                new ColdDrink(),
                Set.of(new SugarSweetened()),
                Drink_Size.XXL,
                new ArrayList<>()
        );

        d.addOrder(oldOd);
        d.setOrder(oldOd, newOd);

        List<Order_Drink> drinkOrders =
                TestUtils.getField(d, "orders", List.class);

        assertFalse(drinkOrders.contains(oldOd));
        assertTrue(drinkOrders.contains(newOd));
    }

    // ----- Coffee -----

    @Test
    void addCoffee_throws_whenCaffeineLevelOutOfRange() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () -> d.addCoffee(-1));
        assertThrows(ValidationException.class, () -> d.addCoffee(11));
    }

    @Test
    void addCoffee_addsTypeAndSetsCaffeineLevel_andDoesNotDuplicateType() {
        Drink d = new Drink("D", 5.0, "none");

        d.addCoffee(7);
        d.addCoffee(3);

        assertTrue(d.getDrinkTypes().contains(models.utils.DrinkType.COFFEE));
        assertEquals(1, d.getDrinkTypes().stream()
                .filter(t -> t == models.utils.DrinkType.COFFEE)
                .count());
        assertEquals(3, d.getCaffeineLevel());
    }

    @Test
    void removeCoffee_removesTypeAndResetsCaffeineLevel() {
        Drink d = new Drink("D", 5.0, "none");
        d.addCoffee(6);

        d.removeCoffee();

        assertFalse(d.getDrinkTypes().contains(models.utils.DrinkType.COFFEE));
        assertEquals(0, d.getCaffeineLevel());
    }

    // ----- Tea -----

    @Test
    void addTea_throws_whenTypeNull() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () -> d.addTea(null));
    }

    @Test
    void addTea_addsTypeAndSetsTeaType_andDoesNotDuplicateType() {
        Drink d = new Drink("D", 5.0, "none");

        models.utils.TypeOfTea t1 = models.utils.TypeOfTea.values()[0];
        models.utils.TypeOfTea t2 = models.utils.TypeOfTea.values().length > 1
                ? models.utils.TypeOfTea.values()[1]
                : t1;

        d.addTea(t1);
        d.addTea(t2);

        assertTrue(d.getDrinkTypes().contains(models.utils.DrinkType.TEA));
        assertEquals(1, d.getDrinkTypes().stream()
                .filter(t -> t == models.utils.DrinkType.TEA)
                .count());
        assertEquals(t2, d.getTypeOfTea());
    }

    @Test
    void removeTea_removesTypeAndResetsTeaType() {
        Drink d = new Drink("D", 5.0, "none");
        d.addTea(models.utils.TypeOfTea.values()[0]);

        d.removeTea();

        assertFalse(d.getDrinkTypes().contains(models.utils.DrinkType.TEA));
        assertNull(d.getTypeOfTea());
    }

    // ----- Milk -----

    @Test
    void addMilk_throws_whenMilkNull() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () -> d.addMilk(null));
    }

    @Test
    void addMilk_addsTypeAndSetsMilkType_andDoesNotDuplicateType() {
        Drink d = new Drink("D", 5.0, "none");

        models.utils.TypeOfMilk m1 = models.utils.TypeOfMilk.values()[0];
        models.utils.TypeOfMilk m2 = models.utils.TypeOfMilk.values().length > 1
                ? models.utils.TypeOfMilk.values()[1]
                : m1;

        d.addMilk(m1);
        d.addMilk(m2);

        assertTrue(d.getDrinkTypes().contains(models.utils.DrinkType.MILK));
        assertEquals(1, d.getDrinkTypes().stream()
                .filter(t -> t == models.utils.DrinkType.MILK)
                .count());
        assertEquals(m2, d.getTypeOfMilk());
    }

    @Test
    void removeMilk_removesTypeAndResetsMilkType() {
        Drink d = new Drink("D", 5.0, "none");
        d.addMilk(models.utils.TypeOfMilk.values()[0]);

        d.removeMilk();

        assertFalse(d.getDrinkTypes().contains(models.utils.DrinkType.MILK));
        assertNull(d.getTypeOfMilk());
    }

    // ----- Fruit / Pulp -----

    @Test
    void addFruits_throws_whenNullOrEmpty() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () -> d.addFruits(null, true));
        assertThrows(ValidationException.class, () -> d.addFruits(new ArrayList<>(), false));
    }

    @Test
    void addFruits_addsType_setsFruitsAndPulp() {
        Drink d = new Drink("D", 5.0, "none");
        List<String> fruits = new ArrayList<>(List.of("apple", "banana"));

        d.addFruits(fruits, true);

        assertTrue(d.getDrinkTypes().contains(models.utils.DrinkType.FRUIT));
        assertEquals(fruits, d.getFruits());

        Boolean pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertTrue(pulp);
    }

    @Test
    void addFruit_throws_whenNullOrBlank() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () -> d.addFruit(null, true));
        assertThrows(ValidationException.class, () -> d.addFruit("", false));
        assertThrows(ValidationException.class, () -> d.addFruit("   ", false));
    }

    @Test
    void addFruit_initializesList_addsFruit_setsPulp_andAddsType() {
        Drink d = new Drink("D", 5.0, "none");

        d.addFruit("apple", true);

        assertTrue(d.getDrinkTypes().contains(models.utils.DrinkType.FRUIT));
        assertNotNull(d.getFruits());
        assertEquals(1, d.getFruits().size());
        assertTrue(d.getFruits().contains("apple"));

        Boolean pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertTrue(pulp);
        d.addFruit("banana", false);

        assertEquals(2, d.getFruits().size());
        assertTrue(d.getFruits().containsAll(List.of("apple", "banana")));

        pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertFalse(pulp);
    }

    @Test
    void removeFruit_throws_whenNullOrBlank() {
        Drink d = new Drink("D", 5.0, "none");

        assertThrows(ValidationException.class, () -> d.removeFruit(null));
        assertThrows(ValidationException.class, () -> d.removeFruit(""));
        assertThrows(ValidationException.class, () -> d.removeFruit("   "));
    }

    @Test
    void removeFruit_removesSingleItem_butKeepsTypeWhenStillHasFruits() {
        Drink d = new Drink("D", 5.0, "none");
        d.addFruits(new ArrayList<>(List.of("apple", "banana")), true);
        d.removeFruit("banana");

        assertTrue(d.getDrinkTypes().contains(models.utils.DrinkType.FRUIT));
        assertNotNull(d.getFruits());
        assertEquals(1, d.getFruits().size());
        assertEquals(List.of("apple"), d.getFruits());

        Boolean pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertTrue(pulp);
    }

    @Test
    void removeFruit_whenLastFruitRemoved_clearsFruits_resetsPulp_andRemovesType() {
        Drink d = new Drink("D", 5.0, "none");
        d.addFruit("apple", true);
        d.removeFruit("apple");

        assertFalse(d.getDrinkTypes().contains(models.utils.DrinkType.FRUIT));
        assertNull(d.getFruits());

        Boolean pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertFalse(pulp);
    }

    @Test
    void removeFruits_clearsFruits_resetsPulp_andRemovesType() {
        Drink d = new Drink("D", 5.0, "none");
        d.addFruits(new ArrayList<>(List.of("apple", "banana")), true);
        d.removeFruits();

        assertFalse(d.getDrinkTypes().contains(models.utils.DrinkType.FRUIT));
        assertNull(d.getFruits());

        Boolean pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertFalse(pulp);
    }

    @Test
    void addPulp_and_removePulp_togglePulpFlag() {
        Drink d = new Drink("D", 5.0, "none");
        Boolean pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertFalse(pulp);

        d.addPulp();
        pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertTrue(pulp);

        d.removePulp();
        pulp = TestUtils.getField(d, "pulp", Boolean.class);
        assertFalse(pulp);
    }
}