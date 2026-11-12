package com.tavernasorte.model;

import com.tavernasorte.exception.InvalidBetException;

import java.math.BigDecimal;

public record RouletteBet(RouletteBetType type, Integer chosenNumber, BigDecimal amount) {

    public static final int DECIMAL_PLACES = 2;
    public static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");

    public RouletteBet {
        if (type == null) {
            throw new InvalidBetException("O tipo de aposta deve ser NUMBER, RED ou BLACK");
        }
        if (type == RouletteBetType.NUMBER) {
            if (chosenNumber == null || chosenNumber < RouletteColor.MIN_NUMBER
                    || chosenNumber > RouletteColor.MAX_NUMBER) {
                throw new InvalidBetException("O número escolhido deve estar entre 0 e 36");
            }
        } else if (chosenNumber != null) {
            throw new InvalidBetException("Apostas em cor não devem informar um número");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidBetException("O valor da aposta deve ser obrigatório e maior que zero");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidBetException("A aposta não pode ultrapassar 10000.00");
        }
        if (amount.scale() > DECIMAL_PLACES) {
            throw new InvalidBetException("A aposta deve ter no máximo 2 casas decimais");
        }
        amount = amount.setScale(DECIMAL_PLACES);
    }

    public boolean wins(int drawnNumber) {
        return type.wins(chosenNumber, drawnNumber);
    }

    /** Retorno total incluindo a aposta original; zero quando a aposta perde. */
    public BigDecimal prizeFor(int drawnNumber) {
        return wins(drawnNumber)
                ? amount.multiply(BigDecimal.valueOf(type.getTotalReturnMultiplier()))
                : BigDecimal.ZERO.setScale(DECIMAL_PLACES);
    }
}
