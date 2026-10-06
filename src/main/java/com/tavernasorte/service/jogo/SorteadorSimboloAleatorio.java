package com.tavernasorte.service.jogo;

import com.tavernasorte.model.jogo.bau.Simbolo;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SorteadorSimboloAleatorio implements SorteadorSimbolo {

    private static final Simbolo[] SIMBOLOS = Simbolo.values();

    private final Random random;

    public SorteadorSimboloAleatorio() {
        this(new Random());
    }

    SorteadorSimboloAleatorio(Random random) {
        this.random = random;
    }

    @Override
    public Simbolo sortear() {
        return SIMBOLOS[random.nextInt(SIMBOLOS.length)];
    }
}
