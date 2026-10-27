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

    private static final int QUANTIDADE_SIMBOLOS = 3;

    private final SymbolPicker sorteadorSymbol;

    public EnchantedChestService(SymbolPicker sorteadorSymbol) {
        this.sorteadorSymbol = sorteadorSymbol;
    }

    public EnchantedChestResponse abrir(BigDecimal valorAposta) {
        ChestBetValue aposta = new ChestBetValue(valorAposta);

        List<Symbol> simbolos = new ArrayList<>();
        for (int i = 0; i < QUANTIDADE_SIMBOLOS; i++) {
            simbolos.add(sorteadorSymbol.sortear());
        }

        return new EnchantedChestResponse(simbolos, calcularPremio(simbolos, aposta));
    }

    private BigDecimal calcularPremio(List<Symbol> simbolos, ChestBetValue aposta) {
        int simbolosDistintos = new HashSet<>(simbolos).size();

        if (simbolosDistintos == 1) {
            return aposta.valor().multiply(BigDecimal.valueOf(simbolos.get(0).getMultiplicador()));
        }
        if (simbolosDistintos == 2) {
            return aposta.valor(); // par de símbolos iguais devolve a aposta
        }
        return BigDecimal.ZERO.setScale(ChestBetValue.CASAS_DECIMAIS);
    }
}
