package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.model.Symbol;

import java.math.BigDecimal;
import java.util.List;

public class EnchantedChestResponse {

    @JsonProperty("simbolos")
    private final List<Symbol> symbols;
    @JsonProperty("premio")
    private final BigDecimal prize;

    public EnchantedChestResponse(List<Symbol> symbols, BigDecimal prize) {
        this.symbols = List.copyOf(symbols);
        this.prize = prize;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public BigDecimal getPrize() {
        return prize;
    }
}
