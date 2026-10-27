package com.tavernasorte.model;

public enum Symbol {
    MOEDA(3),
    POCAO(5),
    ESPADA(8),
    COROA(12),
    DRAGAO(20);

    private final int multiplicador;

    Symbol(int multiplicador) {
        this.multiplicador = multiplicador;
    }

    public int getMultiplicador() {
        return multiplicador;
    }
}
