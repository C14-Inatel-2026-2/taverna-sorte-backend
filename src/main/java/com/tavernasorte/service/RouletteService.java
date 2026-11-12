package com.tavernasorte.service;

import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.model.RouletteBet;
import com.tavernasorte.model.RouletteBetType;
import com.tavernasorte.model.RouletteColor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        return random.nextInt(RouletteColor.NUMBER_COUNT) + RouletteColor.MIN_NUMBER;
    }

    public RoulettePlayResponse play(RouletteBetType type, Integer chosenNumber, BigDecimal amount) {
        RouletteBet bet = new RouletteBet(type, chosenNumber, amount);
        int drawnNumber = spin();
        return new RoulettePlayResponse(bet, drawnNumber, RouletteColor.fromNumber(drawnNumber),
                bet.wins(drawnNumber), bet.prizeFor(drawnNumber));
    }
}
