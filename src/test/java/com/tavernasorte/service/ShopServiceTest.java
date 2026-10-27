package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ItemRepository itemRepository;
    private ShopService lojaService;

    @Test
    void comprarItemTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(lojaService.comprarItem(jogador, 1L));
    }

    @Test
    void saldoPlayerAposCompraTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
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
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(100);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertTrue(jogador.getInventario().getItens().contains(item));
    }

    @Test
    void comprarItemPlayerNuloTeste() {
        lojaService = new ShopService(itemRepository);

        assertFalse(lojaService.comprarItem(null, 1L));
    }

    @Test
    void comprarItemIdNuloTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();

        assertFalse(lojaService.comprarItem(jogador, null));
    }

    @Test
    void saldoPlayerAposItemIdNuloTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(100);

        lojaService.comprarItem(jogador, null);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void comprarItemInexistenteTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertFalse(lojaService.comprarItem(jogador, 99L));
    }

    @Test
    void saldoPlayerAposItemInexistenteTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        lojaService.comprarItem(jogador, 99L);

        assertEquals(100, jogador.getSaldo());
    }

    @Test
    void inventarioVazioPlayerAposCompraItemInexistenteTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        lojaService.comprarItem(jogador, 99L);

        assertTrue(jogador.getInventario().getItens().isEmpty());
    }

    @Test
    void comprarItemSaldoInsuficienteTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(20);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertFalse(lojaService.comprarItem(jogador, 1L));
    }

    @Test
    void saldoPlayerAposSaldoInsuficienteTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(20);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertEquals(20, jogador.getSaldo());
    }

    @Test
    void inventarioVazioPlayerAposSaldoInsuficienteTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
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
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
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
    void saldoPlayerAposCompraInventarioCheioTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
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
    void inventarioPlayerAposCompraInventarioCheioTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
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
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(50);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(lojaService.comprarItem(jogador, 1L));
    }

    @Test
    void saldoPlayerCompraItemComSaldoExatoTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(50);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertEquals(0, jogador.getSaldo());
    }

    @Test
    void inventarioPlayerCompraItemComSaldoExatoTeste() {
        lojaService = new ShopService(itemRepository);
        Player jogador = new Player();
        jogador.adicionarSaldo(50);

        Item item = new Item();
        item.setPreco(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lojaService.comprarItem(jogador, 1L);

        assertTrue(jogador.getInventario().getItens().contains(item));
    }
}