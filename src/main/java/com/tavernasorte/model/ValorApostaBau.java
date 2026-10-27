package com.tavernasorte.model;

import com.tavernasorte.exception.ApostaInvalidaException;

import java.math.BigDecimal;

/** Valor da aposta em unidades da moeda do jogo, com até duas casas decimais. */
public record ValorApostaBau(BigDecimal valor) {

    public static final int DIGITOS_INTEIROS = 12;
    public static final int CASAS_DECIMAIS = 2;
    public static final BigDecimal MAXIMO = new BigDecimal("999999999999.99");
    public static final String FORMATO_INVALIDO =
            "A aposta deve ter no máximo 12 dígitos inteiros e 2 casas decimais";

    public ValorApostaBau {
        if (valor == null) {
            throw new ApostaInvalidaException("O valor da aposta é obrigatório");
        }
        if (valor.signum() <= 0) {
            throw new ApostaInvalidaException("O valor da aposta deve ser maior que zero");
        }
        if (valor.scale() > CASAS_DECIMAIS || valor.compareTo(MAXIMO) > 0) {
            throw new ApostaInvalidaException(FORMATO_INVALIDO);
        }
        valor = valor.setScale(CASAS_DECIMAIS);
    }
}
