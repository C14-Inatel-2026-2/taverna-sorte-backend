package com.tavernasorte.service;

import com.tavernasorte.entity.Simbolo;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SorteadorSimboloAleatorio implements SorteadorSimbolo {

    private static final Simbolo[] SIMBOLOS = Simbolo.values();

    private final Random random = new Random();

    @Override
    public Simbolo sortear() {
        return SIMBOLOS[random.nextInt(SIMBOLOS.length)];
    }
}
