package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.service.EnchantedChestService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EnchantedChestRequest {

    @JsonProperty("playerId")
    @NotNull(message = EnchantedChestService.MISSING_PLAYER)
    private Long playerId;

    @JsonProperty("betAmount")
    @NotNull(message = "O valor da aposta é obrigatório")
    @Positive(message = "O valor da aposta deve ser maior que zero")
    @Max(value = ChestBetValue.MAXIMUM, message = ChestBetValue.INVALID_MAXIMUM_VALUE)
    private Integer betAmount;

    /** Opcional: item do inventário do jogador que multiplica o prêmio. */
    @JsonProperty("itemId")
    private Long itemId;

    public EnchantedChestRequest() {
    }

    public EnchantedChestRequest(Long playerId, Integer betAmount, Long itemId) {
        this.playerId = playerId;
        this.betAmount = betAmount;
        this.itemId = itemId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
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

    /** Torna o corpo estrito: qualquer campo fora do contrato rejeita a requisição. */
    @JsonAnySetter
    private void rejectUnknownField(String name, Object value) {
        throw new IllegalArgumentException("Campo desconhecido: " + name);
    }
}
