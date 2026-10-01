package com.tavernasorte.dto;

public class ApostaBauRequest {

    private double valor;

    public ApostaBauRequest() {
    }

    public ApostaBauRequest(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
