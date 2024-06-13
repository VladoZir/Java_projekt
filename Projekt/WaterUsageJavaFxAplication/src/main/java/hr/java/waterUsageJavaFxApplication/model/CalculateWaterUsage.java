package hr.java.waterUsageJavaFxApplication.model;

import java.math.BigDecimal;

public sealed interface CalculateWaterUsage permits Tenant, Shower, Dishwasher, WashingMachine, CarWash {
    BigDecimal getTotalWaterUsed();
}
