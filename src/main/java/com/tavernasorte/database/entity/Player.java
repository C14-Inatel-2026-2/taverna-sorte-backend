package com.tavernasorte.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saldo")
    @JsonProperty("saldo")
    private int balance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventario_id")
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

