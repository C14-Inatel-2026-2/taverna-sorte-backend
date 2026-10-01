package com.tavernasorte.dto;

import com.tavernasorte.entity.Simbolo;

import java.util.List;

public class ResultadoBau {

    private final List<Simbolo> simbolos;
    private final double premio;

    public ResultadoBau(List<Simbolo> simbolos, double premio) {
        this.simbolos = List.copyOf(simbolos);
        this.premio = premio;
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }

    public double getPremio() {
        return premio;
    }
}
