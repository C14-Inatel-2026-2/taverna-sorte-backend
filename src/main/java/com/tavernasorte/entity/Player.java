package com.tavernasorte.entity;

public class Player {

    private int saldo;
    private Inventory inventario;
    private String username;

    public Player() {
        this.saldo = 0;
        this.inventario = new Inventory();
    }

    public int getSaldo() {
        return saldo;
    }

    public Inventory getInventory() {
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

