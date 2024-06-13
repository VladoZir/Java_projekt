package hr.java.waterUsage.model;

import java.io.Serializable;
import java.math.BigDecimal;

public non-sealed class WashingMachine extends Activity implements CalculateWaterUsage, Serializable {
    //public static BigDecimal waterUsedPerMinute = BigDecimal.valueOf(5.5);
    private BigDecimal durationMinutes;

    public WashingMachine(BigDecimal durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(BigDecimal durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getTotalWaterUsed(){
        return this.durationMinutes.multiply(getLitersPerMinute());
    }

    @Override
    public BigDecimal getLitersPerMinute() {
        return BigDecimal.valueOf(6);
    }
}
