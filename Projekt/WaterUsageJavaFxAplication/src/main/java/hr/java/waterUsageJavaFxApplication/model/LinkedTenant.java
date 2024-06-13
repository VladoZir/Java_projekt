package hr.java.waterUsageJavaFxApplication.model;

public class LinkedTenant {
    private Long householdId;
    private Long tenantId;

    public LinkedTenant(Long householdId, Long tenantId) {
        this.householdId = householdId;
        this.tenantId = tenantId;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
