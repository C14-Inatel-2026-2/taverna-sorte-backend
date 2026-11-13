package com.tavernasorte.service;

import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.model.Symbol;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class EnchantedChestService {

    private static final int SYMBOL_COUNT = 3;

    private final SymbolPicker symbolPicker;

    public EnchantedChestService(SymbolPicker symbolPicker) {
        this.symbolPicker = symbolPicker;
    }

    public EnchantedChestResponse open(Integer betAmount) {
        ChestBetValue bet = new ChestBetValue(betAmount);

        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < SYMBOL_COUNT; i++) {
            symbols.add(symbolPicker.pick());
        }

        return new EnchantedChestResponse(symbols, calculatePrize(symbols, bet));
    }

    private int calculatePrize(List<Symbol> symbols, ChestBetValue bet) {
        int distinctSymbolCount = new HashSet<>(symbols).size();

        if (distinctSymbolCount == 1) {
            return bet.value() * symbols.get(0).getMultiplier();
        }
        if (distinctSymbolCount == 2) {
            return bet.value(); // par de símbolos iguais devolve a aposta
        }
        return 0;
    }
}
