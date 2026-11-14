package com.tavernasorte.dto;

import com.tavernasorte.model.Symbol;

import java.util.List;

public class EnchantedChestResponse {

    private final List<Symbol> symbols;
    private final int prize;
    private final int balance;

    public EnchantedChestResponse(List<Symbol> symbols, int prize, int balance) {
        this.symbols = List.copyOf(symbols);
        this.prize = prize;
        this.balance = balance;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public int getPrize() {
        return prize;
    }

    public int getBalance() {
        return balance;
    }
}
