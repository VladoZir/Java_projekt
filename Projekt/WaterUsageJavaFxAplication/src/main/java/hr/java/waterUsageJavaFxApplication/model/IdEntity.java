package hr.java.waterUsageJavaFxApplication.model;

import java.io.Serializable;

public abstract class IdEntity implements Serializable {
    private Long id;

    public IdEntity(Long id) {
        this.id = id;
    }

    public IdEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
