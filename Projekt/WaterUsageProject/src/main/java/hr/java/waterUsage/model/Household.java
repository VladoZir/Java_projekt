package hr.java.waterUsage.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class Household extends IdEntity implements Leakable, Serializable {
    public static final BigDecimal LITER_OF_WATER_PRICE = BigDecimal.valueOf(0.00168);

    public static class Builder{
        private Long id;
        private String address;
        private List<Tenant> tenantList;
        private BigDecimal totalHouseholdWaterUsage;
        private Leak leak;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder atAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder tenantList(List<Tenant> tenantList) {
            this.tenantList = tenantList;
            return this;
        }

        public Builder totalHouseholdWaterUsage(BigDecimal totalHouseholdWaterUsage) {
            this.totalHouseholdWaterUsage = totalHouseholdWaterUsage;
            return this;
        }

        public Builder leak(Leak leak) {
            this.leak = leak;
            return this;
        }

        public Household build() {
            Household household = new Household();
            household.setId(this.id);
            household.address = this.address;
            household.tenantList = this.tenantList;
            household.totalHouseholdWaterUsage = this.totalHouseholdWaterUsage;
            household.leak = this.leak;

            return household;
        }
    }


    private String address;
    private List<Tenant> tenantList;
    private BigDecimal totalHouseholdWaterUsage;
    private Leak leak;

    private Household() {
    }

    private Household(Long id, String address, List<Tenant> tenantList, BigDecimal totalHouseholdWaterUsage) {
        super(id);
        this.address = address;
        this.tenantList = tenantList;
        this.totalHouseholdWaterUsage = totalHouseholdWaterUsage;
    }

    public List<Tenant> getTenantList() {
        return tenantList;
    }

    public void setTenantList(List<Tenant> tenantList) {
        this.tenantList = tenantList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getTotalHouseholdWaterUsage() {
        return totalHouseholdWaterUsage;
    }

    public void setTotalHouseholdWaterUsage(BigDecimal totalHouseholdWaterUsage) {
        this.totalHouseholdWaterUsage = totalHouseholdWaterUsage;
    }

    public Leak getLeak() {
        return leak;
    }

    public void setLeak(Leak leak) {
        this.leak = leak;
    }

    public BigDecimal calculateTotalBill(){
        if (this.getLeak().isLeaking()){
            return this.getTotalHouseholdWaterUsage().multiply(LITER_OF_WATER_PRICE).multiply(BigDecimal.valueOf(1.15));
        }else {
            return this.getTotalHouseholdWaterUsage().multiply(LITER_OF_WATER_PRICE);
        }
    }

    @Override
    public Leak determineLeakingStatus() {
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        return new Leak(randomBoolean);
    }
}
