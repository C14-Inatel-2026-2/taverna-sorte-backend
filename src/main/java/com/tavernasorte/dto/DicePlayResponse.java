package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.model.BetType;

public class DicePlayResponse {

    @JsonProperty("dado1")
    private final int die1;
    @JsonProperty("dado2")
    private final int die2;
    @JsonProperty("soma")
    private final int sum;
    @JsonProperty("tipoAposta")
    private final BetType betType;
    @JsonProperty("venceu")
    private final boolean won;
    @JsonProperty("premio")
    private final double prize;

    public DicePlayResponse(int die1, int die2, int sum, BetType betType,
                            boolean won, double prize) {
        this.die1 = die1;
        this.die2 = die2;
        this.sum = sum;
        this.betType = betType;
        this.won = won;
        this.prize = prize;
    }

    public int getDie1() {
        return die1;
    }

    public int getDie2() {
        return die2;
    }

    public int getSum() {
        return sum;
    }

    public BetType getBetType() {
        return betType;
    }

    public boolean isWon() {
        return won;
    }

    public double getPrize() {
        return prize;
    }
}
