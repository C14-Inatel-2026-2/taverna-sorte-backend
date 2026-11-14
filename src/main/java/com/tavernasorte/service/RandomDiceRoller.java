package com.tavernasorte.service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomDiceRoller implements DiceRoller {

    private static final int SIDES = 6;

    private final Random random;

    public RandomDiceRoller() {
        this(new Random());
    }

    RandomDiceRoller(Random random) {
        this.random = random;
    }

    @Override
    public int roll() {
        return random.nextInt(SIDES) + 1;
    }
}
