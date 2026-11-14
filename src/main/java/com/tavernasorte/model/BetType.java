package com.tavernasorte.model;

public enum BetType {
    LOW(2),
    SEVEN(4),
    HIGH(2);

    private final int multiplier;

    BetType(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
