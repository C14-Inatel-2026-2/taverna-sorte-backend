package com.tavernasorte.model;

public enum RouletteBetType {
    NUMBER(36), RED(2), BLACK(2);

    private final int multiplier;

    RouletteBetType(int multiplier) {
        this.multiplier = multiplier;
    }

    /** Retorno total, incluindo as moedas apostadas. */
    public int getMultiplier() {
        return multiplier;
    }
}
