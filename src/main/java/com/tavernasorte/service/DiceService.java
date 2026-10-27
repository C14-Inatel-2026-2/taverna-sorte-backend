package com.tavernasorte.service;

import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.model.BetType;
import org.springframework.stereotype.Service;

@Service
public class DiceService {

    private final DiceRoller diceRoller;

    public DiceService(DiceRoller diceRoller) {
        this.diceRoller = diceRoller;
    }

    public DicePlayResponse play(BetType betType, double betAmount) {
        if (betType == null) {
            throw new IllegalArgumentException("O tipo de aposta deve ser LOW, SEVEN ou HIGH");
        }
        if (!Double.isFinite(betAmount) || betAmount <= 0) {
            throw new IllegalArgumentException("O valor da aposta deve ser maior que zero e finito");
        }

        int die1 = diceRoller.roll();
        int die2 = diceRoller.roll();
        int sum = die1 + die2;
        boolean won = isWinningBet(betType, sum);
        double prize = calculatePrize(betType, betAmount, won);

        return new DicePlayResponse(die1, die2, sum, betType, won, prize);
    }

    private boolean isWinningBet(BetType betType, int sum) {
        return switch (betType) {
            case LOW -> sum >= 2 && sum <= 6;
            case SEVEN -> sum == 7;
            case HIGH -> sum >= 8 && sum <= 12;
        };
    }

    private double calculatePrize(BetType betType, double betAmount, boolean won) {
        if (!won) {
            return 0;
        }
        return betAmount * (betType == BetType.SEVEN ? 4 : 2);
    }
}
