package com.tavernasorte.dto;

import java.math.BigDecimal;

public record RoulettePlayResponse(
        int chosenNumber,
        int drawnNumber,
        BigDecimal betAmount,
        boolean won,
        BigDecimal prize
) {
}
