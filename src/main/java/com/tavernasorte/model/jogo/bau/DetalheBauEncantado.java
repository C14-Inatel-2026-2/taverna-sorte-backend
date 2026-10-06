package com.tavernasorte.model.jogo.bau;

import java.util.List;

public record DetalheBauEncantado(List<Simbolo> simbolos) {

    public DetalheBauEncantado {
        simbolos = List.copyOf(simbolos);
    }
}
