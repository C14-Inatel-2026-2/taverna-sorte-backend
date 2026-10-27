package com.tavernasorte.service;

import com.tavernasorte.model.Symbol;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomSymbolPicker implements SymbolPicker {

    private static final Symbol[] SIMBOLOS = Symbol.values();

    private final Random random;

    public RandomSymbolPicker() {
        this(new Random());
    }

    RandomSymbolPicker(Random random) {
        this.random = random;
    }

    @Override
    public Symbol sortear() {
        return SIMBOLOS[random.nextInt(SIMBOLOS.length)];
    }
}
