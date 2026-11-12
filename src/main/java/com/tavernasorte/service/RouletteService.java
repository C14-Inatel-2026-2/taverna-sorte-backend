package com.tavernasorte.service;

import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.model.RouletteBet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.random.RandomGenerator;

@Service
public class RouletteService {

    private final RandomGenerator random;

    public RouletteService() {
        this(new Random());
    }

    RouletteService(RandomGenerator random) {
        this.random = random;
    }

    public int spin() {
        return random.nextInt(RouletteBet.NUMBER_COUNT) + RouletteBet.MIN_NUMBER;
    }

    public RoulettePlayResponse play(Integer chosenNumber, BigDecimal betAmount) {
        RouletteBet bet = new RouletteBet(chosenNumber, betAmount);
        int drawnNumber = spin();
        return new RoulettePlayResponse(bet.number(), drawnNumber, bet.amount(),
                bet.wins(drawnNumber), bet.prizeFor(drawnNumber));
    }
}
