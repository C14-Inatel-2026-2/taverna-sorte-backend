package com.tavernasorte.service;

import com.tavernasorte.dto.BauEncantadoResponse;
import com.tavernasorte.model.Simbolo;
import com.tavernasorte.model.ValorApostaBau;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class BauEncantadoService {

    private static final int QUANTIDADE_SIMBOLOS = 3;

    private final SorteadorSimbolo sorteadorSimbolo;

    public BauEncantadoService(SorteadorSimbolo sorteadorSimbolo) {
        this.sorteadorSimbolo = sorteadorSimbolo;
    }

    public BauEncantadoResponse abrir(BigDecimal valorAposta) {
        ValorApostaBau aposta = new ValorApostaBau(valorAposta);

        List<Simbolo> simbolos = new ArrayList<>();
        for (int i = 0; i < QUANTIDADE_SIMBOLOS; i++) {
            simbolos.add(sorteadorSimbolo.sortear());
        }

        return new BauEncantadoResponse(simbolos, calcularPremio(simbolos, aposta));
    }

    private BigDecimal calcularPremio(List<Simbolo> simbolos, ValorApostaBau aposta) {
        int simbolosDistintos = new HashSet<>(simbolos).size();

        if (simbolosDistintos == 1) {
            return aposta.valor().multiply(BigDecimal.valueOf(simbolos.get(0).getMultiplicador()));
        }
        if (simbolosDistintos == 2) {
            return aposta.valor(); // par de símbolos iguais devolve a aposta
        }
        return BigDecimal.ZERO.setScale(ValorApostaBau.CASAS_DECIMAIS);
    }
}
