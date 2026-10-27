package com.tavernasorte.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.exception.InvalidBetException;

import java.math.BigDecimal;

/** Valor da aposta em unidades da moeda do jogo, com até duas casas decimais. */
public record ChestBetValue(@JsonProperty("valor") BigDecimal value) {

    public static final int INTEGER_DIGITS = 12;
    public static final int DECIMAL_PLACES = 2;
    public static final BigDecimal MAXIMUM = new BigDecimal("999999999999.99");
    public static final String INVALID_FORMAT =
            "A aposta deve ter no máximo 12 dígitos inteiros e 2 casas decimais";

    public ChestBetValue {
        if (value == null) {
            throw new InvalidBetException("O valor da aposta é obrigatório");
        }
        if (value.signum() <= 0) {
            throw new InvalidBetException("O valor da aposta deve ser maior que zero");
        }
        if (value.scale() > DECIMAL_PLACES || value.compareTo(MAXIMUM) > 0) {
            throw new InvalidBetException(INVALID_FORMAT);
        }
        value = value.setScale(DECIMAL_PLACES);
    }
}
