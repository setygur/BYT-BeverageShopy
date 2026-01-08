package models;

import models.aspects.SweetenerAspect;
import models.aspects.TemperatureAspect;
import models.utils.DrinkType;
import models.utils.Drink_Size;
import models.utils.TypeOfMilk;
import models.utils.TypeOfTea;
import persistence.JsonCtor;
import persistence.JsonSerializable;
import persistence.ObjectList;
import validation.*;

import java.util.*;

@JsonSerializable
public class Drink implements Validatable {
    @ObjectList
    public static List<Drink> drinks = new ArrayList<>();

    //Flattening discriminator
    private List<DrinkType> drinkTypes = new ArrayList<>();

    @NotBlank
    private String name;

    @NotNull
    @Range(min = 0)
    public double basePrice;

    @NotBlank
    private String persistentAllergens;

    private final List<Order_Drink> orders = new ArrayList<>();

    /*

    Classes fields

     */

    //Coffee fields
    private int caffeineLevel;

    //Tea fields
    private TypeOfTea typeOfTea;

    //Milk fields
    private TypeOfMilk typeOfMilk;

    //Fruit fields
    private List<String> fruits;
    private boolean pulp;

    // Deliveries 0..*
    private List<Delivery> deliveries = new ArrayList<>();

    // ---------------- Constructor ----------------

    @JsonCtor
    public Drink(
            String name,
            double basePrice,
            String persistentAllergens
    ) {
        this.name = name;
        this.basePrice = basePrice;
        this.persistentAllergens = persistentAllergens;

        try {
            if (!validate(this)) throw new ValidationException("Invalid data");
        } catch (IllegalAccessException | ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        drinks.add(this);
    }

    // ---------------- Orders (association class) ----------------

    public void addOrder(
            Order order,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        if (order == null) throw new ValidationException("Invalid data");
        if (temperature == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");

        Set<SweetenerAspect> safeSweeteners =
                (sweeteners == null) ? Collections.emptySet() : new HashSet<>(sweeteners);

        List<String> safeToppings = new ArrayList<>(toppings);

        Order_Drink od = Order_Drink.find(
                order,
                this,
                temperature,
                safeSweeteners,
                size,
                safeToppings
        );

        if (od == null) {
            od = new Order_Drink(
                    order,
                    this,
                    temperature,
                    safeSweeteners,
                    size,
                    safeToppings
            );
        }

        addOrder(od);
    }

    public void addOrder(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if (orders.contains(od)) return;

        orders.add(od);
        od.getOrder().addDrink(od); // sync inverse
    }

    public void removeOrder(
            Order order,
            TemperatureAspect temperature,
            Set<SweetenerAspect> sweeteners,
            Drink_Size size,
            List<String> toppings
    ) {
        if (order == null) throw new ValidationException("Invalid data");
        if (temperature == null) throw new ValidationException("Invalid data");
        if (size == null) throw new ValidationException("Invalid data");
        if (toppings == null) throw new ValidationException("Invalid data");

        Set<SweetenerAspect> safeSweeteners =
                (sweeteners == null) ? Collections.emptySet() : new HashSet<>(sweeteners);

        Order_Drink od = Order_Drink.find(
                order,
                this,
                temperature,
                safeSweeteners,
                size,
                toppings
        );

        if (od != null) {
            removeOrder(od);
        }
    }

    public void removeOrder(Order_Drink od) {
        if (od == null) throw new ValidationException("Invalid data");
        if (!orders.contains(od)) return;

        orders.remove(od);
        od.getOrder().removeDrink(od); // sync inverse
    }

    public void setOrder(Order_Drink oldOd, Order_Drink newOd) {
        if (oldOd == null) throw new ValidationException("Invalid data");
        if (newOd == null) throw new ValidationException("Invalid data");
        if (!orders.contains(oldOd)) return;

        orders.remove(oldOd);
        orders.add(newOd);

        oldOd.getOrder().setDrink(oldOd, newOd); // sync inverse
    }

    // ---------------- Deliveries ----------------

    public void addDelivery(Delivery delivery) {
        if (delivery != null && !deliveries.contains(delivery)) {
            deliveries.add(delivery);
            delivery.addDrink(this);
        }
    }

    public void removeDelivery(Delivery delivery) {
        if (delivery != null && deliveries.contains(delivery)) {
            deliveries.remove(delivery);
            delivery.removeDrink(this);
        }
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }

    //Type logic, add / remove
    //Coffee
    public void addCoffee(int caffeineLevel){
        if(caffeineLevel < 0 || caffeineLevel > 10) throw new ValidationException("Invalid data");
        if(!this.drinkTypes.contains(DrinkType.COFFEE)) this.drinkTypes.add(DrinkType.COFFEE);
        this.caffeineLevel = caffeineLevel;
    }

    public void removeCoffee(){
        this.drinkTypes.remove(DrinkType.COFFEE);
        this.caffeineLevel = 0;
    }

    //Tea
    public void addTea(TypeOfTea typeOfTea){
        if(typeOfTea == null) throw new ValidationException("Invalid data");
        if(!this.drinkTypes.contains(DrinkType.TEA))  this.drinkTypes.add(DrinkType.TEA);
        this.typeOfTea = typeOfTea;
    }

    public void removeTea(){
        this.drinkTypes.remove(DrinkType.TEA);
        this.typeOfTea = null;
    }

    //Milk
    public void addMilk(TypeOfMilk milk){
        if(milk == null) throw new ValidationException("Invalid data");
        if(!this.drinkTypes.contains(DrinkType.MILK))  this.drinkTypes.add(DrinkType.MILK);
        this.typeOfMilk = milk;
    }

    public void removeMilk(){
        this.drinkTypes.remove(DrinkType.MILK);
        this.typeOfMilk = null;
    }

    //Fruit
    public void addFruits(List<String> fruits, boolean pulp){
        if(fruits == null || fruits.isEmpty()) throw new ValidationException("Invalid data");
        if(!this.drinkTypes.contains(DrinkType.FRUIT))  this.drinkTypes.add(DrinkType.FRUIT);
        this.fruits = fruits;
        this.pulp = pulp;
    }

    public void addFruit(String fruit, boolean pulp){
        if(fruit == null || fruit.isEmpty()) throw new ValidationException("Invalid data");
        if(!this.drinkTypes.contains(DrinkType.FRUIT)) this.drinkTypes.add(DrinkType.FRUIT);
        if(this.fruits == null) this.fruits = new ArrayList<>();
        this.fruits.add(fruit);
        this.pulp = pulp;
    }

    public void removeFruit(String fruit){
        if(fruit == null || fruit.isEmpty()) throw new ValidationException("Invalid data");
        this.fruits.remove(fruit);
        if(this.fruits.isEmpty()){
            this.fruits = null;
            this.pulp = false;
            this.drinkTypes.remove(DrinkType.FRUIT);
        }
    }

    public void removeFruits(){
        this.drinkTypes.remove(DrinkType.FRUIT);
        this.fruits = null;
        this.pulp = false;
    }

    public void addPulp(){
        this.pulp = true;
    }

    public void removePulp(){
        this.pulp = false;
    }

    public List<DrinkType> getDrinkTypes() {
        return drinkTypes;
    }

    public void setDrinkTypes(List<DrinkType> drinkTypes) {
        this.drinkTypes = drinkTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getPersistentAllergens() {
        return persistentAllergens;
    }

    public void setPersistentAllergens(String persistentAllergens) {
        this.persistentAllergens = persistentAllergens;
    }

    public int getCaffeineLevel() {
        return caffeineLevel;
    }

    public TypeOfTea getTypeOfTea() {
        return typeOfTea;
    }

    public TypeOfMilk getTypeOfMilk() {
        return typeOfMilk;
    }

    public List<String> getFruits() {
        return fruits;
    }

}