package com.tavernasorte.service;

import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.entity.TipoAposta;
import org.springframework.stereotype.Service;

@Service
public class DiceService {

    private final DiceRoller diceRoller;

    public DiceService(DiceRoller diceRoller) {
        this.diceRoller = diceRoller;
    }

    public DicePlayResponse play(TipoAposta tipoAposta, double valorAposta) {
        if (tipoAposta == null) {
            throw new IllegalArgumentException("O tipo de aposta deve ser LOW, SEVEN ou HIGH");
        }
        if (!Double.isFinite(valorAposta) || valorAposta <= 0) {
            throw new IllegalArgumentException("O valor da aposta deve ser maior que zero e finito");
        }

        int dado1 = diceRoller.roll();
        int dado2 = diceRoller.roll();
        int soma = dado1 + dado2;
        boolean venceu = venceu(tipoAposta, soma);
        double premio = calcularPremio(tipoAposta, valorAposta, venceu);

        return new DicePlayResponse(dado1, dado2, soma, tipoAposta, venceu, premio);
    }

    private boolean venceu(TipoAposta tipoAposta, int soma) {
        return switch (tipoAposta) {
            case LOW -> soma >= 2 && soma <= 6;
            case SEVEN -> soma == 7;
            case HIGH -> soma >= 8 && soma <= 12;
        };
    }

    private double calcularPremio(TipoAposta tipoAposta, double valorAposta, boolean venceu) {
        if (!venceu) {
            return 0;
        }
        return valorAposta * (tipoAposta == TipoAposta.SEVEN ? 4 : 2);
    }
}
