package com.tavernasorte.model.jogo;

/**
 * @param multiplicador quanto do valor apostado volta para o jogador (0 = perdeu, 2.0 = dobrou)
 * @param descricao     texto para exibir ao jogador
 * @param detalhe       o que foi sorteado, para o front animar
 */
public record Resultado(double multiplicador, String descricao, Object detalhe) {

    public Resultado {
        if (multiplicador < 0) {
            throw new IllegalArgumentException("O multiplicador não pode ser negativo");
        }
    }
}
