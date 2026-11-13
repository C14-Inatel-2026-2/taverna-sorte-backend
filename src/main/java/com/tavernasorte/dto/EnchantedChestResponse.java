package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.model.Symbol;

import java.util.List;

public class EnchantedChestResponse {

    @JsonProperty("symbols")
    private final List<Symbol> symbols;
    @JsonProperty("prize")
    private final int prize;
    @JsonProperty("balance")
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
