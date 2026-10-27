package com.tavernasorte.database.entity;

import com.tavernasorte.entity.Item;
import com.tavernasorte.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void saldoPlayerNovoTeste() {
        Player jogador = new Player();
        assertEquals(0, jogador.getSaldo());
    }

    @Test
    void inventarioVazioPlayerNovoTeste() {
        Player jogador = new Player();
        assertTrue(jogador.getInventory().getItens().isEmpty());
    }

    @Test
    void adicionarSaldoTeste() {
        Player jogador = new Player();

        jogador.adicionarSaldo(100);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void adicionarSaldoNegativoTeste() {
        Player jogador = new Player();

        jogador.adicionarSaldo(-50);

        assertEquals(0, jogador.getSaldo());
    }

    @Test
    void debitarSaldoTeste() {
        Player jogador = new Player();
        jogador.adicionarSaldo(100);
        jogador.debitar(30);

        assertEquals(70, jogador.getSaldo());
    }

    @Test
    void debitarSaldoInsuficienteTeste() {
        Player jogador = new Player();
        jogador.adicionarSaldo(20);
        jogador.debitar(30);

        assertEquals(20, jogador.getSaldo());
    }

    @Test
    void jogadorPossuiItemNoInventoryTeste() {
        Player jogador = new Player();
        Item item = new Item();

        jogador.getInventory().adicionarItem(item);

        assertTrue(jogador.getInventory().getItens().contains(item));
    }
}
