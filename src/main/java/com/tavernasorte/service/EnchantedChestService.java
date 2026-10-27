package com.tavernasorte.service;

import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.model.Symbol;
import com.tavernasorte.model.ChestBetValue;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public EnchantedChestResponse open(BigDecimal betAmount) {
        ChestBetValue bet = new ChestBetValue(betAmount);

        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < SYMBOL_COUNT; i++) {
            symbols.add(symbolPicker.pick());
        }

        return new EnchantedChestResponse(symbols, calculatePrize(symbols, bet));
    }

    private BigDecimal calculatePrize(List<Symbol> symbols, ChestBetValue bet) {
        int distinctSymbolCount = new HashSet<>(symbols).size();

        if (distinctSymbolCount == 1) {
            return bet.value().multiply(BigDecimal.valueOf(symbols.get(0).getMultiplier()));
        }
        if (distinctSymbolCount == 2) {
            return bet.value(); // par de símbolos iguais devolve a aposta
        }
        return BigDecimal.ZERO.setScale(ChestBetValue.DECIMAL_PLACES);
    }
}
