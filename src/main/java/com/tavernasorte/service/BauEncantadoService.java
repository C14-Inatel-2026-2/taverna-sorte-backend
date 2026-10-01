package com.tavernasorte.service;

import com.tavernasorte.dto.ResultadoBau;
import com.tavernasorte.entity.Simbolo;
import org.springframework.stereotype.Service;

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

    public ResultadoBau abrir(double valorAposta) {
        if (valorAposta <= 0) {
            throw new IllegalArgumentException("O valor da aposta deve ser maior que zero");
        }

        List<Simbolo> simbolos = new ArrayList<>();
        for (int i = 0; i < QUANTIDADE_SIMBOLOS; i++) {
            simbolos.add(sorteadorSimbolo.sortear());
        }

        return new ResultadoBau(simbolos, calcularPremio(simbolos, valorAposta));
    }

    public double calcularPremio(List<Simbolo> simbolos, double valorAposta) {
        int simbolosDistintos = new HashSet<>(simbolos).size();

        if (simbolosDistintos == 1) {
            return valorAposta * simbolos.get(0).getMultiplicador();
        }
        if (simbolosDistintos == 2) {
            return valorAposta; // par de símbolos iguais devolve a aposta
        }
        return 0;
    }
}
