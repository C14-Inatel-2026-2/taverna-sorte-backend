package com.tavernasorte.dto;

import com.tavernasorte.model.Symbol;

import java.math.BigDecimal;
import java.util.List;

public class EnchantedChestResponse {

    private final List<Symbol> simbolos;
    private final BigDecimal premio;

    public EnchantedChestResponse(List<Symbol> simbolos, BigDecimal premio) {
        this.simbolos = List.copyOf(simbolos);
        this.premio = premio;
    }

    public List<Symbol> getSymbols() {
        return simbolos;
    }

    public BigDecimal getPremio() {
        return premio;
    }
}
