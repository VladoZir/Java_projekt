package hr.java.waterUsage.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public non-sealed class Tenant extends IdEntity implements CalculateWaterUsage, Serializable {
    private String fullName;
    private Shower shower;
    private Dishwasher dishwasher;
    private WashingMachine washingMachine;
    private CarWash carWash;

    public Tenant(Long id, String fullName, Shower shower, Dishwasher dishwasher, WashingMachine washingMachine, CarWash carWash) {
        super(id);
        this.fullName = fullName;
        this.shower = shower;
        this.dishwasher = dishwasher;
        this.washingMachine = washingMachine;
        this.carWash = carWash;
    }

    public Shower getShower() {
        return shower;
    }

    public void setShower(Shower shower) {
        this.shower = shower;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Dishwasher getDishwasher() {
        return dishwasher;
    }

    public void setDishwasher(Dishwasher dishwasher) {
        this.dishwasher = dishwasher;
    }

    public WashingMachine getWashingMachine() {
        return washingMachine;
    }

    public void setWashingMachine(WashingMachine washingMachine) {
        this.washingMachine = washingMachine;
    }

    public CarWash getCarWash() {
        return carWash;
    }
    public void setCarWash(CarWash carWash) {
        this.carWash = carWash;
    }

    @Override
    public BigDecimal getTotalWaterUsed() {
        return this.getShower().getTotalWaterUsed()
                .add(this.getDishwasher().getTotalWaterUsed())
                .add(this.getWashingMachine().getTotalWaterUsed())
                .add(this.getCarWash().getTotalWaterUsed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(fullName, tenant.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName);
    }
}
