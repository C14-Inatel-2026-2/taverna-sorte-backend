package com.tavernasorte.model;

/** Símbolos do baú; o nome da constante é o valor enviado no JSON. */
public enum Symbol {
    COIN(3),
    POTION(5),
    SWORD(8),
    CROWN(12),
    DRAGON(20);

    private final int multiplier;

    Symbol(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
