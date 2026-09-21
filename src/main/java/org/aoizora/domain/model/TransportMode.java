package org.aoizora.domain.model;

public enum TransportMode {
    WALK,
    BUS,
    TRAM,
    TRAIN;

    public static TransportMode fromName(String name) {
        if (name == null || name.isBlank()) {
            return WALK;
        }
        try {
            return valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return WALK;
        }
    }
}
