package com.tavernasorte.model;

import com.tavernasorte.exception.InvalidBetException;

/**
 * Valor da aposta em moedas inteiras do jogo, entre 1 e {@value #MAXIMUM}.
 */
public record ChestBetValue(Integer value) {

    public static final int MAXIMUM = 10000;
    public static final String INVALID_MAXIMUM_VALUE = "O valor máximo da aposta é " + MAXIMUM;

    public ChestBetValue {
        if (value == null) {
            throw new InvalidBetException("O valor da aposta é obrigatório");
        }
        if (value <= 0) {
            throw new InvalidBetException("O valor da aposta deve ser maior que zero");
        }
        if (value > MAXIMUM) {
            throw new InvalidBetException(INVALID_MAXIMUM_VALUE);
        }
    }
}
