package com.tavernasorte.entity;

public enum Simbolo {
    MOEDA(3),
    POCAO(5),
    ESPADA(8),
    COROA(12),
    DRAGAO(20);

    private final int multiplicador;

    Simbolo(int multiplicador) {
        this.multiplicador = multiplicador;
    }

    public int getMultiplicador() {
        return multiplicador;
    }
}
