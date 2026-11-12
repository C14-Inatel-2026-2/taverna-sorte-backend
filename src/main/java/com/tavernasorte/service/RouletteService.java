package com.tavernasorte.service;

import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.model.RouletteBetType;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

@Service
public class RouletteService {

    private static final int MIN_NUMBER = 0;
    private static final int MAX_NUMBER = 36;
    private static final int NUMBER_COUNT = MAX_NUMBER - MIN_NUMBER + 1;
    private static final Set<Integer> RED_NUMBERS = Set.of(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);

    private final Random random;

    public RouletteService() {
        this(new Random());
    }

    RouletteService(Random random) {
        this.random = random;
    }

    public int spin() {
        return random.nextInt(NUMBER_COUNT) + MIN_NUMBER;
    }

    public RoulettePlayResponse play(RouletteBetType betType, Integer chosenNumber, int betAmount) {
        validateBet(betType, chosenNumber, betAmount);
        int winningPrize = calculateWinningPrize(betType, betAmount);
        int drawnNumber = spin();
        String color = colorOf(drawnNumber);
        boolean won = switch (betType) {
            case NUMBER -> chosenNumber == drawnNumber;
            case RED -> color.equals("RED");
            case BLACK -> color.equals("BLACK");
        };

        return new RoulettePlayResponse(betType, chosenNumber, betAmount, drawnNumber,
                color, won, won ? winningPrize : 0);
    }

    private void validateBet(RouletteBetType betType, Integer chosenNumber, int betAmount) {
        if (betType == null) {
            throw new InvalidBetException("O tipo de aposta deve ser NUMBER, RED ou BLACK");
        }
        if (betType == RouletteBetType.NUMBER) {
            if (chosenNumber == null || chosenNumber < MIN_NUMBER || chosenNumber > MAX_NUMBER) {
                throw new InvalidBetException("O número escolhido deve estar entre 0 e 36");
            }
        } else if (chosenNumber != null) {
            throw new InvalidBetException("Apostas em cor não devem informar um número");
        }
        if (betAmount <= 0) {
            throw new InvalidBetException("A aposta deve ser um número inteiro positivo de moedas");
        }
    }

    private int calculateWinningPrize(RouletteBetType betType, int betAmount) {
        try {
            return Math.multiplyExact(betAmount, betType.getMultiplier());
        } catch (ArithmeticException exception) {
            throw new InvalidBetException("A aposta excede o limite de moedas do prêmio");
        }
    }

    private String colorOf(int number) {
        if (number == 0) {
            return "GREEN";
        }
        return RED_NUMBERS.contains(number) ? "RED" : "BLACK";
    }
}
