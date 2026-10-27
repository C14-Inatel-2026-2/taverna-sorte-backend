package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tavernasorte.model.ChestBetValue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class EnchantedChestRequest {

    @JsonAlias("valor")
    @NotNull(message = "O valor da aposta é obrigatório")
    @Positive(message = "O valor da aposta deve ser maior que zero")
    @Digits(integer = ChestBetValue.DIGITOS_INTEIROS,
            fraction = ChestBetValue.CASAS_DECIMAIS,
            message = ChestBetValue.FORMATO_INVALIDO)
    private BigDecimal valorAposta;

    public EnchantedChestRequest() {
    }

    public EnchantedChestRequest(BigDecimal valorAposta) {
        this.valorAposta = valorAposta;
    }

    public BigDecimal getValorAposta() {
        return valorAposta;
    }

    public void setValorAposta(BigDecimal valorAposta) {
        this.valorAposta = valorAposta;
    }
}
