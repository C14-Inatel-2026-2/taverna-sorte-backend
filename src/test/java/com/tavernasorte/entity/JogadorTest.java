package com.tavernasorte.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JogadorTest {

    @Test
    void saldoJogadorNovoTeste() {
        Jogador jogador = new Jogador();
        assertEquals(0, jogador.getSaldo());
    }

    @Test
    void inventarioVazioJogadorNovoTeste() {
        Jogador jogador = new Jogador();
        assertTrue(jogador.getInventario().getItens().isEmpty());
    }

    @Test
    void adicionarSaldoTeste() {
        Jogador jogador = new Jogador();

        jogador.adicionarSaldo(100);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void adicionarSaldoNegativoTeste() {
        Jogador jogador = new Jogador();

        jogador.adicionarSaldo(-50);

        assertEquals(0, jogador.getSaldo());
    }

    @Test
    void debitarSaldoTeste() {
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);
        jogador.debitar(30);

        assertEquals(70, jogador.getSaldo());
    }

    @Test
    void debitarSaldoInsuficienteTeste() {
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(20);
        jogador.debitar(30);

        assertEquals(20, jogador.getSaldo());
    }

    @Test
    void jogadorPossuiItemNoInventarioTeste() {
        Jogador jogador = new Jogador();
        Item item = new Item();

        jogador.getInventario().adicionarItem(item);

        assertTrue(jogador.getInventario().getItens().contains(item));
    }
}
