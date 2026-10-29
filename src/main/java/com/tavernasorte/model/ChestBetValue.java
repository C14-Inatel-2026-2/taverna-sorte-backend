package com.tavernasorte.model;

import com.tavernasorte.exception.InvalidBetException;

import java.math.BigDecimal;

/**
 * Valor da aposta em unidades da moeda do jogo, com até duas casas decimais
 * e valor máximo de {@value #MAXIMUM_TEXT}.
 */
public record ChestBetValue(BigDecimal value) {

    private static final String MAXIMUM_TEXT = "10000.00";

    public static final int INTEGER_DIGITS = 5;
    public static final int DECIMAL_PLACES = 2;
    public static final BigDecimal MAXIMUM = new BigDecimal(MAXIMUM_TEXT);
    public static final String INVALID_FORMAT = "A aposta deve ter no máximo " + INTEGER_DIGITS
            + " dígitos inteiros e " + DECIMAL_PLACES + " casas decimais";
    public static final String INVALID_MAXIMUM_VALUE = "O valor máximo da aposta é " + MAXIMUM_TEXT;

    public ChestBetValue {
        if (value == null) {
            throw new InvalidBetException("O valor da aposta é obrigatório");
        }
        if (value.signum() <= 0) {
            throw new InvalidBetException("O valor da aposta deve ser maior que zero");
        }
        if (value.scale() > DECIMAL_PLACES || value.precision() - value.scale() > INTEGER_DIGITS) {
            throw new InvalidBetException(INVALID_FORMAT);
        }
        if (value.compareTo(MAXIMUM) > 0) {
            throw new InvalidBetException(INVALID_MAXIMUM_VALUE);
        }
        value = value.setScale(DECIMAL_PLACES);
    }
}
