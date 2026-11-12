package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.tavernasorte.model.BetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DicePlayRequest {

    @NotNull(message = "O jogador é obrigatório")
    @Positive(message = "O identificador do jogador deve ser positivo")
    private Long playerId;

    @NotNull(message = "O tipo de aposta deve ser LOW, SEVEN ou HIGH")
    private BetType betType;

    @NotNull(message = "O valor da aposta é obrigatório")
    @Min(value = 1, message = "A aposta mínima é 1 moeda")
    @Max(value = 10000, message = "A aposta máxima é 10000 moedas")
    private Integer betAmount;

    @Positive(message = "O identificador do item deve ser positivo")
    private Long itemId;

    public DicePlayRequest() {
    }

    public DicePlayRequest(Long playerId, BetType betType, Integer betAmount, Long itemId) {
        this.playerId = playerId;
        this.betType = betType;
        this.betAmount = betAmount;
        this.itemId = itemId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public BetType getBetType() {
        return betType;
    }

    public void setBetType(BetType betType) {
        this.betType = betType;
    }

    public Integer getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Integer betAmount) {
        this.betAmount = betAmount;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

}
