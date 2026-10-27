package com.tavernasorte.dto;

import com.tavernasorte.entity.BetType;
import jakarta.validation.constraints.Positive;

public class DicePlayRequest {


    private BetType tipoAposta;

    @Positive(message = "O valor da aposta deve ser maior que zero")
    private double valorAposta;

    public DicePlayRequest() {
    }

    public DicePlayRequest(BetType tipoAposta, double valorAposta) {
        this.tipoAposta = tipoAposta;
        this.valorAposta = valorAposta;
    }

    public BetType getTipoAposta() {
        return tipoAposta;
    }

    public void setTipoAposta(BetType tipoAposta) {
        this.tipoAposta = tipoAposta;
    }

    public double getValorAposta() {
        return valorAposta;
    }

    public void setValorAposta(double valorAposta) {
        this.valorAposta = valorAposta;
    }
}
