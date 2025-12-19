package models;

import models.utils.EmployeeType;
import persistence.JsonCtor;
import persistence.ObjectList;
import validation.*;

import java.util.*;
import java.util.stream.Collectors;

public class Employee extends Person {

    @ObjectList
    public static List<Employee> employees = new ArrayList<>();


    private String peselNumber;
    private String passportNumber;

    @Range(min = 0)
    private static double baseSalary = 3000.0; // Default base

    //Flattening discriminator
    private EmployeeType type = EmployeeType.NONE;

    //Common associations
    public List<Shift> shifts = new ArrayList<>();

    //composition 0..*
    @ObjectList
    private final List<Certification> certifications = new ArrayList<>();

    // References of who managing and training this employee
    private Employee manager;
    private Employee trainer;

    /*

    Classes fields

     */

    //loader fields
    @Range(min = 0)
    private Double loaderEvaluationScore;
    private List<Delivery> deliveries;

    // manager fields
    @Range(min = 0)
    private Double managerEvaluationScore;
    @Range(min = 0)
    private Double bonusPercent;
    private List<Employee> managed;
    private List<Employee> trained;

    // cashier fields
    private Boolean handlesCash;
    @Unique
    private String cashierId;
    @Range(min = 0)
    private Double cashierEvaluationScore;
    private List<Order> orders;


    @JsonCtor
    public Employee(String name, String surname, String email,
                    String peselNumber, String passportNumber) {
        super(name, surname, email);
        this.peselNumber = peselNumber;
        this.passportNumber = passportNumber;

        // custom invariant
        if (peselNumber == null && passportNumber == null)
            throw new ValidationException("Either PESEL or passport must be provided");

        employees.add(this);
    }


    //Switching logic
    //Erase role-specific data to prevent zombie state
    private void clearRoles() {
        // 1. Cleanup Loader
        if (this.type == EmployeeType.LOADER && deliveries != null) {
            for (Delivery d : new ArrayList<>(deliveries)) {
                d.removeLoader(this);
            }
            this.deliveries = null;
            this.loaderEvaluationScore = null;
        }

        // 2. Cleanup Manager
        if (this.type == EmployeeType.MANAGER && managed != null) {
            // Unlink subordinates
            for (Employee e : new ArrayList<>(managed)) {
                e.removeManager(this);
            }
            this.managed = null;

            // Unlink trainees
            if (trained != null) {
                for (Employee e : new ArrayList<>(trained)) {
                    e.removeTrainer(this);
                }
                this.trained = null;
            }
            this.managerEvaluationScore = null;
            this.bonusPercent = null;
        }

        // 3. Cleanup Cashier
        if (this.type == EmployeeType.CASHIER && orders != null) {
            for (Order o : new ArrayList<>(orders)) {
                o.removeCashier(this);
            }
            this.orders = null;
            this.handlesCash = null;
            this.cashierId = null;
            this.cashierEvaluationScore = null;
        }

        this.type = EmployeeType.NONE;
    }

    public void becomeLoader(double score) {
        if (score < 0) throw new ValidationException("Score cannot be negative");

        clearRoles(); // Wipe previous identity

        this.type = EmployeeType.LOADER;
        this.loaderEvaluationScore = score;
        this.deliveries = new ArrayList<>();
    }

    public void becomeManager(double score, double bonusPercent) {
        if (score < 0) throw new ValidationException("Score cannot be negative");
        if (bonusPercent < 0) throw new ValidationException("Bonus cannot be negative");

        clearRoles(); // Wipe previous identity

        this.type = EmployeeType.MANAGER;
        this.managerEvaluationScore = score;
        this.bonusPercent = bonusPercent;
        this.managed = new ArrayList<>();
        this.trained = new ArrayList<>();
    }

    public void becomeCashier(boolean handlesCash, String cashierId, double score) {
        if (cashierId == null || cashierId.isBlank()) throw new ValidationException("Cashier ID required");
        if (score < 0) throw new ValidationException("Score cannot be negative");

        // Unique check for Cashier ID would typically go here or in a service
        // For simplicity, we assume uniqueness is checked externally or via annotation processor

        clearRoles(); // Wipe previous identity

        this.type = EmployeeType.CASHIER;
        this.handlesCash = handlesCash;
        this.cashierId = cashierId;
        this.cashierEvaluationScore = score;
        this.orders = new ArrayList<>();
    }

    public EmployeeType getType() {
        return type;
    }

    // role dependent
    public double getSalary() {
        switch (this.type) {
            case LOADER:
                return calculateLoaderSalary();
            case MANAGER:
                return calculateManagerSalary();
            case CASHIER:
                return calculateCashierSalary();
            default:
                return baseSalary; // Fallback for basic employee
        }
    }

   // Loader methods
    private double calculateLoaderSalary() {
        // Constants logic
        double hourlyRate = 25.0;
        double deliveryBonus = 125.0;

        // Mock calculation of hours from shifts
        double totalHours = shifts.size() * 8.0;

        return (totalHours * hourlyRate) + (deliveries.size() * deliveryBonus);
    }

    public void addDelivery(Delivery delivery) {
        if (this.type != EmployeeType.LOADER) {
            throw new ValidationException("Only a Loader can perform this operation.");
        }
        if (delivery == null) throw new ValidationException("Invalid data");

        if (!deliveries.contains(delivery)) {
            // Delivery must be updated to accept generic Employee or we cast externally
            // Assuming Delivery.addLoader(Employee e) exists
            if(delivery.addLoader(this)){
                deliveries.add(delivery);
            }
        }
    }

    public void removeDelivery(Delivery delivery) {
        if (this.type != EmployeeType.LOADER) return;
        if (delivery == null) throw new ValidationException("Invalid data");

        if (deliveries.contains(delivery)) {
            deliveries.remove(delivery);
            delivery.removeLoader(this);
        }
    }

    public List<Delivery> getDeliveries() {
        if (this.type != EmployeeType.LOADER) return Collections.emptyList();
        return Collections.unmodifiableList(deliveries);
    }

    //manager methods
    private double calculateManagerSalary() {
        double base = 1200.0; // mocked base
        return base + (base * (bonusPercent / 100.0));
    }

    public void addManaged(Employee employee) {
        if (this.type != EmployeeType.MANAGER) {
            throw new ValidationException("Only a Manager can perform this operation.");
        }
        if(employee == null) throw new ValidationException("Invalid data");

        if(!this.managed.contains(employee)){
            this.managed.add(employee);
            employee.setManager(this);
        }
    }

    public void removeManaged(Employee employee) {
        if (this.type != EmployeeType.MANAGER) return;
        if(employee == null) throw new ValidationException("Invalid data");

        if(this.managed.contains(employee)){
            this.managed.remove(employee);
            employee.removeManager(this);
        }
    }

    public void addTrainee(Employee trainee) {
        if (this.type != EmployeeType.MANAGER) {
            throw new ValidationException("Only a Manager can perform this operation.");
        }
        if (trainee == null) throw new ValidationException("Invalid data");

        // only train people you manage
        if (!this.managed.contains(trainee)) {
            throw new ValidationException("This Trainer does not manage this Employee");
        }

        if (!this.trained.contains(trainee)) {
            this.trained.add(trainee);
            trainee.addTrainer(this);
        }
    }

    public void removeTrainee(Employee trainee) {
        if (this.type != EmployeeType.MANAGER) return;
        if (trainee == null) throw new ValidationException("Invalid data");

        if (this.trained.contains(trainee)) {
            this.trained.remove(trainee);
            trainee.removeTrainer(this);
        }
    }

    // manager method
    private double calculateCashierSalary() {
        double hourlyRate = 20.0;
        double tips = 150.0; // Mocked
        double totalHours = shifts.size() * 8.0;

        return (totalHours * hourlyRate) + tips + (handlesCash ? 100.0 : 0.0);
    }

    public void addOrder(Order order) {
        if (this.type != EmployeeType.CASHIER) {
            throw new ValidationException("Only a Cashier can perform this operation.");
        }
        if (order == null) throw new ValidationException("Invalid data");

        if(!orders.contains(order)) {
            orders.add(order);
            order.addCashier(this);
        }
    }

    public void removeOrder(Order order) {
        if (this.type != EmployeeType.CASHIER) return;
        if (order == null) throw new ValidationException("Invalid data");

        if (orders.contains(order)) {
            orders.remove(order);
            order.removeCashier(this);
        }
    }

    // generic methods
    public void setManager(Employee manager) {
        if(manager != null && manager.getType() != EmployeeType.MANAGER) {
            throw new ValidationException("Assigned supervisor must be a Manager type");
        }

        if(this.manager != null && this.manager != manager){
            this.manager.removeManaged(this);
        }

        this.manager = manager;

        if(this.manager != null && !this.manager.managed.contains(this)){
            this.manager.addManaged(this);
        }
    }

    public void removeManager(Employee manager) {
        if(this.manager == manager) {
            this.manager = null;
        }
    }


    public void addTrainer(Employee trainer) {
        if(trainer != null && trainer.getType() != EmployeeType.MANAGER) {
            throw new ValidationException("Trainer must be a Manager type");
        }
        if (this.trainer == trainer) return;

        this.trainer = trainer;
        if (trainer != null) {
            trainer.addTrainee(this);
        }
    }

    public void removeTrainer(Employee trainer) {
        if (this.trainer == trainer) {
            this.trainer = null;
        }
    }


    public void addShift(Shift shift) {
        if (!shifts.contains(shift)) {
            shifts.add(shift);
        }
    }
    public List<Shift> getShifts() {
        return Collections.unmodifiableList(shifts);
    }

    public List<Certification> getCertifications() {
        return Collections.unmodifiableList(certifications);
    }

    public void addCertification(Certification certification) {
        if (!certifications.contains(certification)) {
            certifications.add(certification);
        }
    }

    public void removeCertification(Certification certification) {
        if (certifications.contains(certification)) {
            certifications.remove(certification);
            certification.removeConnection();
        }
    }

    @Override
    public String toString() {
        String roleInfo = "None";
        if(type == EmployeeType.LOADER) roleInfo = "Loader (Deliveries: " + (deliveries != null ? deliveries.size() : 0) + ")";
        if(type == EmployeeType.MANAGER) roleInfo = "Manager (Subordinates: " + (managed != null ? managed.size() : 0) + ")";
        if(type == EmployeeType.CASHIER) roleInfo = "Cashier (ID: " + cashierId + ")";

        return new StringJoiner(", ")
                .add(super.toString())
                .add("PESEL: " + peselNumber)
                .add("Role: " + roleInfo)
                .toString();
    }
}