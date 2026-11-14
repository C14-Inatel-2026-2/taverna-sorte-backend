package com.tavernasorte.dto;

import com.tavernasorte.model.BetType;

public record DicePlayResponse(
        int die1,
        int die2,
        int sum,
        BetType betType,
        boolean won,
        int prize,
        int balance,
        Long consumedItemId,
        int itemMultiplier
) {
}
