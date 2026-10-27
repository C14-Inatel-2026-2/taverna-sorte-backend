package com.tavernasorte.service;

import com.tavernasorte.entity.Item;
import com.tavernasorte.entity.Jogador;
import com.tavernasorte.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LojaServiceTest {

    @Mock
    private ItemRepository itemRepository;
    private LojaService lojaService;

    @Test
    void comprarItemTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(lojaService.comprarItem(jogador, 1L));
    }

    @Test
    void saldoJogadorAposCompraTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertEquals(50, jogador.getSaldo());
    }

    @Test
    void jogadorTemItemAposCompraTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertTrue(jogador.getInventario().getItens().contains(item));
    }

    @Test
    void comprarItemJogadorNuloTeste() {
        lojaService = new LojaService(itemRepository);

        assertFalse(lojaService.comprarItem(null, 1L));
    }

    @Test
    void comprarItemIdNuloTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();

        assertFalse(lojaService.comprarItem(jogador, null));
    }

    @Test
    void saldoJogadorAposItemIdNuloTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        lojaService.comprarItem(jogador, null);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void comprarItemInexistenteTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertFalse(lojaService.comprarItem(jogador, 99L));
    }

    @Test
    void saldoJogadorAposItemInexistenteTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        lojaService.comprarItem(jogador, 99L);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void inventarioVazioJogadorAposCompraItemInexistenteTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        lojaService.comprarItem(jogador, 99L);

        assertTrue(jogador.getInventario().getItens().isEmpty());
    }

    @Test
    void comprarItemSaldoInsuficienteTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(20);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertFalse(lojaService.comprarItem(jogador, 1L));
    }

    @Test
    void saldoJogadorAposSaldoInsuficienteTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(20);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertEquals(20, jogador.getSaldo());
    }

    @Test
    void inventarioVazioJogadorAposSaldoInsuficienteTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(20);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertTrue(jogador.getInventario().getItens().isEmpty());
    }

    @Test
    void comprarItemInventarioCheioTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(10);

        jogador.getInventario().adicionarItem(item);
        jogador.getInventario().adicionarItem(item);
        jogador.getInventario().adicionarItem(item);

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        assertFalse(lojaService.comprarItem(jogador, 4L));
    }

    @Test
    void saldoJogadorAposCompraInventarioCheioTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(10);

        jogador.getInventario().adicionarItem(item);
        jogador.getInventario().adicionarItem(item);
        jogador.getInventario().adicionarItem(item);

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 4L);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void inventarioJogadorAposCompraInventarioCheioTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(10);

        jogador.getInventario().adicionarItem(item);
        jogador.getInventario().adicionarItem(item);
        jogador.getInventario().adicionarItem(item);

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 4L);

        assertEquals(3, jogador.getInventario().getItens().size());
    }

    @Test
    void comprarItemComSaldoExatoTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(50);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(lojaService.comprarItem(jogador, 1L));
    }

    @Test
    void saldoJogadorCompraItemComSaldoExatoTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(50);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertEquals(0, jogador.getSaldo());
    }

    @Test
    void inventarioJogadorCompraItemComSaldoExatoTeste() {
        lojaService = new LojaService(itemRepository);
        Jogador jogador = new Jogador();
        jogador.adicionarSaldo(50);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertTrue(jogador.getInventario().getItens().contains(item));
    }
}