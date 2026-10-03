package com.tavernasorte.dto;

import com.tavernasorte.entity.TipoAposta;

public class DicePlayResponse {

    private final int dado1;
    private final int dado2;
    private final int soma;
    private final TipoAposta tipoAposta;
    private final boolean venceu;
    private final double premio;

    public DicePlayResponse(int dado1, int dado2, int soma, TipoAposta tipoAposta,
                            boolean venceu, double premio) {
        this.dado1 = dado1;
        this.dado2 = dado2;
        this.soma = soma;
        this.tipoAposta = tipoAposta;
        this.venceu = venceu;
        this.premio = premio;
    }

    public int getDado1() {
        return dado1;
    }

    public int getDado2() {
        return dado2;
    }

    public int getSoma() {
        return soma;
    }

    public TipoAposta getTipoAposta() {
        return tipoAposta;
    }

    public boolean isVenceu() {
        return venceu;
    }

    public double getPremio() {
        return premio;
    }
}
