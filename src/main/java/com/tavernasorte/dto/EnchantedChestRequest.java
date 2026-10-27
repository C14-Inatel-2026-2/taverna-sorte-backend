package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.model.ChestBetValue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class EnchantedChestRequest {

    @JsonAlias("valor")
    @JsonProperty("valorAposta")
    @NotNull(message = "O valor da aposta é obrigatório")
    @Positive(message = "O valor da aposta deve ser maior que zero")
    @Digits(integer = ChestBetValue.INTEGER_DIGITS,
            fraction = ChestBetValue.DECIMAL_PLACES,
            message = ChestBetValue.INVALID_FORMAT)
    private BigDecimal betAmount;

    public EnchantedChestRequest() {
    }

    public EnchantedChestRequest(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }
}
