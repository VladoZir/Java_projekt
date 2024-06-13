package hr.java.waterUsageJavaFxApplication.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeSet;

public class Changes implements Serializable {
    private String changeDescription;
    private LocalDateTime date;
    private String role;

    public Changes(String changeDescription, LocalDateTime date, String role) {
        this.changeDescription = changeDescription;
        this.date = date;
        this.role = role;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
