package hr.java.waterUsageJavaFxApplication.model;

import java.io.Serializable;
import java.util.List;

public class LeakingHouseholdGeneric<T extends Household> implements Serializable {
    private List<T> leakingHouseholdList;

    public LeakingHouseholdGeneric(List<T> leakingHouseholdList) {
        this.leakingHouseholdList = leakingHouseholdList;
    }

    public List<T> getLeakingHouseholdList() {
        return leakingHouseholdList;
    }

    public void setLeakingHouseholdList(List<T> leakingHouseholdList) {
        this.leakingHouseholdList = leakingHouseholdList;
    }
}
