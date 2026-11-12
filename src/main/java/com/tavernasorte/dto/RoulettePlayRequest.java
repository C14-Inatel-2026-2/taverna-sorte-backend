package com.tavernasorte.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoulettePlayRequest(
        @NotNull(message = "O número escolhido é obrigatório") Integer chosenNumber,
        @NotNull(message = "O valor da aposta é obrigatório") BigDecimal betAmount
) {
}
