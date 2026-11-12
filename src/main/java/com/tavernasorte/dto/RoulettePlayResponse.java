package com.tavernasorte.dto;

import com.tavernasorte.model.RouletteBetType;

public record RoulettePlayResponse(
        RouletteBetType betType,
        Integer chosenNumber,
        int betAmount,
        int drawnNumber,
        String color,
        boolean won,
        int prize
) {
}
