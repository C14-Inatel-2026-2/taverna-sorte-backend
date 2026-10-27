package com.tavernasorte.service;

import com.tavernasorte.model.Symbol;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomSymbolPicker implements SymbolPicker {

    private static final Symbol[] SYMBOLS = Symbol.values();

    private final Random random;

    public RandomSymbolPicker() {
        this(new Random());
    }

    RandomSymbolPicker(Random random) {
        this.random = random;
    }

    @Override
    public Symbol pick() {
        return SYMBOLS[random.nextInt(SYMBOLS.length)];
    }
}
