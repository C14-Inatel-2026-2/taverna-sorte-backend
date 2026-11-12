package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record RoulettePlayResponse(
        @JsonProperty("numeroEscolhido") int chosenNumber,
        @JsonProperty("numeroSorteado") int drawnNumber,
        @JsonProperty("valorAposta") BigDecimal betAmount,
        @JsonProperty("venceu") boolean won,
        @JsonProperty("premio") BigDecimal prize
) {
}
