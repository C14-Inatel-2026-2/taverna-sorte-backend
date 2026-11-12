package com.tavernasorte.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RouletteService {

    private static final int MIN_RESULT = 1;
    private static final int MAX_RESULT = 10;
    private static final int RESULT_COUNT = MAX_RESULT - MIN_RESULT + 1;

    private final Random random;

    public RouletteService() {
        this(new Random());
    }

    RouletteService(Random random) {
        this.random = random;
    }

    public int spin() {
        return random.nextInt(RESULT_COUNT) + MIN_RESULT;
    }
}
