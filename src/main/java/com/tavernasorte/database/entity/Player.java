package com.tavernasorte.database.entity;

import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance")
    private int balance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

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

