package hr.java.waterUsage.model;

import java.io.Serializable;

public record Leak(Boolean isLeaking) implements Serializable {
}
