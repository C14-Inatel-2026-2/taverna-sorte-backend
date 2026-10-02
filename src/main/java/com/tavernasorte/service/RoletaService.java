package com.tavernasorte.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RoletaService {

    private final Random random;

    public RoletaService() {
        this(new Random());
    }

    RoletaService(Random random) {
        this.random = random;
    }

    public int girar() {
        return random.nextInt(10) + 1;
    }
}
