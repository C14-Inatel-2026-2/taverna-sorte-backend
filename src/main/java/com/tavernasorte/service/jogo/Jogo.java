package com.tavernasorte.service.jogo;

import com.tavernasorte.model.jogo.Resultado;

/**
 * Contrato para os jogos que adotarem este padrão. Uma jogada decide
 * o resultado sem acessar usuário ou banco de dados.
 *
 * @param <E> tipo da escolha do jogador (um DTO com Bean Validation),
 *            ou {@link Void} quando o jogo não exige escolha
 */
public interface Jogo<E> {

    String id();

    Class<E> tipoEscolha();

    Resultado jogar(E escolha);
}
