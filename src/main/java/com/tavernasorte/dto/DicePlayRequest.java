package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tavernasorte.entity.TipoAposta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DicePlayRequest {


    private TipoAposta tipoAposta;

    @Positive(message = "O valor da aposta deve ser maior que zero")
    private double valorAposta;

    public DicePlayRequest() {
    }

    public DicePlayRequest(TipoAposta tipoAposta, double valorAposta) {
        this.tipoAposta = tipoAposta;
        this.valorAposta = valorAposta;
    }

    public TipoAposta getTipoAposta() {
        return tipoAposta;
    }

    public void setTipoAposta(TipoAposta tipoAposta) {
        this.tipoAposta = tipoAposta;
    }

    public double getValorAposta() {
        return valorAposta;
    }

    public void setValorAposta(double valorAposta) {
        this.valorAposta = valorAposta;
    }
}
