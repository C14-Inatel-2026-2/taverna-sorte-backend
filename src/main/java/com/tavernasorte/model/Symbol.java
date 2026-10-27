package com.tavernasorte.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Symbol {
    @JsonProperty("MOEDA")
    COIN(3),
    @JsonProperty("POCAO")
    POTION(5),
    @JsonProperty("ESPADA")
    SWORD(8),
    @JsonProperty("COROA")
    CROWN(12),
    @JsonProperty("DRAGAO")
    DRAGON(20);

    private final int multiplier;

    Symbol(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
