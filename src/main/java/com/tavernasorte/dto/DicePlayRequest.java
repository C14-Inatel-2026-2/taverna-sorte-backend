package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.tavernasorte.model.BetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DicePlayRequest(
        @NotNull(message = "O jogador é obrigatório")
        @Positive(message = "O identificador do jogador deve ser positivo") Long playerId,
        @NotNull(message = "O tipo de aposta deve ser LOW, SEVEN ou HIGH") BetType betType,
        @NotNull(message = "O valor da aposta é obrigatório")
        @Min(value = 1, message = "A aposta mínima é 1 moeda")
        @Max(value = 10000, message = "A aposta máxima é 10000 moedas") Integer betAmount,
        @Positive(message = "O identificador do item deve ser positivo") Long itemId
) {

    @JsonAnySetter
    private void rejectUnknownField(String name, Object value) {
        throw new IllegalArgumentException("Campo desconhecido: " + name);
    }
}
