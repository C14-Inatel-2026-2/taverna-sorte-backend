package com.tavernasorte.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

    @JsonProperty("saldo")
    private int balance;
    private Inventory inventory;
    private String username;

    public Player() {
        this.balance = 0;
        this.inventory = new Inventory();
    }

    public int getBalance() {
        return balance;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addBalance(int value) {
        if (value > 0) {
            balance += value;
        }
    }

    public boolean debit(int value) {
        if (value <= 0 || balance < value) {
            return false;
        }
        balance -= value;
        return true;
    }

    public String getUsername() {
        return username;
    }
}

