package com.tavernasorte.database.entity;

import com.tavernasorte.exception.InvalidBalanceException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Player {

    public static final int MIN_BALANCE = 0;
    public static final int MAX_BALANCE = 1_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance")
    @Min(MIN_BALANCE)
    @Max(MAX_BALANCE)
    private int balance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    private String username;

    public Player() {
        this.balance = MIN_BALANCE;
        this.inventory = new Inventory();
    }

    public Player(String username) {
        this();
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addBalance(int value) {
        if (value <= 0) {
            throw new InvalidBalanceException("O valor do crédito deve ser maior que zero");
        }
        if (balance < MIN_BALANCE || balance > MAX_BALANCE || value > MAX_BALANCE - balance) {
            throw new InvalidBalanceException("O crédito ultrapassa o saldo máximo de 1000000 moedas");
        }
        balance += value;
    }

    public boolean debit(int value) {
        if (value <= 0 || balance < MIN_BALANCE || balance > MAX_BALANCE || balance < value) {
            return false;
        }
        balance -= value;
        return true;
    }

    public String getUsername() {
        return username;
    }
}

