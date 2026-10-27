package com.tavernasorte.dto;

import com.tavernasorte.model.Simbolo;

import java.math.BigDecimal;
import java.util.List;

public class BauEncantadoResponse {

    private final List<Simbolo> simbolos;
    private final BigDecimal premio;

    public BauEncantadoResponse(List<Simbolo> simbolos, BigDecimal premio) {
        this.simbolos = List.copyOf(simbolos);
        this.premio = premio;
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }

    public BigDecimal getPremio() {
        return premio;
    }
}
