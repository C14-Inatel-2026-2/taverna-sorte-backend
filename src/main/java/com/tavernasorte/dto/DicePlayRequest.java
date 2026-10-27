package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.model.BetType;
import jakarta.validation.constraints.Positive;

public class DicePlayRequest {


    @JsonProperty("tipoAposta")
    private BetType betType;

    @Positive(message = "O valor da aposta deve ser maior que zero")
    @JsonProperty("valorAposta")
    private double betAmount;

    public DicePlayRequest() {
    }

    public DicePlayRequest(BetType betType, double betAmount) {
        this.betType = betType;
        this.betAmount = betAmount;
    }

    public BetType getBetType() {
        return betType;
    }

    public void setBetType(BetType betType) {
        this.betType = betType;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }
}
