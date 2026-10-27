package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tavernasorte.model.ValorApostaBau;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class BauEncantadoRequest {

    @JsonAlias("valor")
    @NotNull(message = "O valor da aposta é obrigatório")
    @Positive(message = "O valor da aposta deve ser maior que zero")
    @Digits(integer = ValorApostaBau.DIGITOS_INTEIROS,
            fraction = ValorApostaBau.CASAS_DECIMAIS,
            message = ValorApostaBau.FORMATO_INVALIDO)
    private BigDecimal valorAposta;

    public BauEncantadoRequest() {
    }

    public BauEncantadoRequest(BigDecimal valorAposta) {
        this.valorAposta = valorAposta;
    }

    public BigDecimal getValorAposta() {
        return valorAposta;
    }

    public void setValorAposta(BigDecimal valorAposta) {
        this.valorAposta = valorAposta;
    }
}
