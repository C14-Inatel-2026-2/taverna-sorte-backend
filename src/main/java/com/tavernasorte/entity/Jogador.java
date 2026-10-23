package com.tavernasorte.entity;

public class Jogador {

    private int saldo;
    private Inventario inventario;
    private String username;

    public Jogador() {
        this.saldo = 0;
        this.inventario = new Inventario();
    }

    public int getSaldo() {
        return saldo;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void adicionarSaldo(int valor) {
        if (valor > 0) {
            saldo += valor;
        }
    }

    public boolean debitar(int valor) {
        if (valor <= 0 || saldo < valor) {
            return false;
        }

        saldo -= valor;
        return true;
    }

    public String getUsername() {
        return username;
    }
}

