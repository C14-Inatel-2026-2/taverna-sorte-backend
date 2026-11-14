package com.tavernasorte.dto;

import com.tavernasorte.model.Symbol;

import java.util.List;

public record EnchantedChestResponse(List<Symbol> symbols, int prize, int balance) {

    public EnchantedChestResponse {
        symbols = List.copyOf(symbols);
    }
}
