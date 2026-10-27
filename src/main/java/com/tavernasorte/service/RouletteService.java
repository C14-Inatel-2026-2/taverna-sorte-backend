package com.tavernasorte.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RouletteService {

    private final Random random;

    public RouletteService() {
        this(new Random());
    }

    RouletteService(Random random) {
        this.random = random;
    }

    public int spin() {
        return random.nextInt(10) + 1;
    }
}
