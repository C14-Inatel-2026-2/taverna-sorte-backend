package com.tavernasorte.model;

import com.tavernasorte.exception.InvalidBetException;

import java.math.BigDecimal;

public record RouletteBet(Integer number, BigDecimal amount) {

    public static final int MIN_NUMBER = 1;
    public static final int MAX_NUMBER = 10;
    public static final int NUMBER_COUNT = MAX_NUMBER - MIN_NUMBER + 1;
    public static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");
    public static final int DECIMAL_PLACES = 2;
    private static final BigDecimal WIN_MULTIPLIER = BigDecimal.valueOf(9);

    public RouletteBet {
        if (number == null || number < MIN_NUMBER || number > MAX_NUMBER) {
            throw new InvalidBetException("O número escolhido deve estar entre 1 e 10");
        }
        if (amount == null) {
            throw new InvalidBetException("O valor da aposta é obrigatório");
        }
        if (amount.signum() <= 0) {
            throw new InvalidBetException("O valor da aposta deve ser maior que zero");
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
        return number == drawnNumber;
    }

    /** Prêmio total, incluindo a aposta original em caso de vitória. */
    public BigDecimal prizeFor(int drawnNumber) {
        return wins(drawnNumber)
                ? amount.multiply(WIN_MULTIPLIER)
                : BigDecimal.ZERO.setScale(DECIMAL_PLACES);
    }
}
