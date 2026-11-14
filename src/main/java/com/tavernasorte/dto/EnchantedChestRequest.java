package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.tavernasorte.model.ChestBetValue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnchantedChestRequest(
        @NotNull(message = "O jogador é obrigatório")
        @Positive(message = "O identificador do jogador deve ser positivo") Long playerId,
        @NotNull(message = "O valor da aposta é obrigatório")
        @Min(value = 1, message = "O valor da aposta deve ser maior que zero")
        @Max(value = ChestBetValue.MAXIMUM, message = ChestBetValue.INVALID_MAXIMUM_VALUE) Integer betAmount,
        @Positive(message = "O identificador do item deve ser positivo") Long itemId
) {

    @JsonAnySetter
    private void rejectUnknownField(String name, Object value) {
        throw new IllegalArgumentException("Campo desconhecido: " + name);
    }
}
