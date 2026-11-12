package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoulettePlayRequest(
        @JsonProperty("numeroEscolhido")
        @NotNull(message = "O número escolhido é obrigatório") Integer chosenNumber,
        @JsonProperty("valorAposta")
        @NotNull(message = "O valor da aposta é obrigatório") BigDecimal betAmount
) {
}
