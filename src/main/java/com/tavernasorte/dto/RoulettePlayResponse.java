package com.tavernasorte.dto;

import com.tavernasorte.model.RouletteBet;
import com.tavernasorte.model.RouletteColor;

import java.math.BigDecimal;

public record RoulettePlayResponse(
        RouletteBet bet,
        int drawnNumber,
        RouletteColor color,
        boolean won,
        BigDecimal prize
) {
}
