package com.tavernasorte.service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomDiceRoller implements DiceRoller {

    private final Random random = new Random();

    @Override
    public int roll() {
        return random.nextInt(6) + 1;
    }
}
