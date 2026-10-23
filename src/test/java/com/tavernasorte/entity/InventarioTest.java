package com.tavernasorte.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventarioTest {

    @Test
    void inventarioVazioTeste() {
        Inventario inventario = new Inventario();
        assertTrue(inventario.getItens().isEmpty());
    }

    @Test
    void adicionarItemTeste() {
        Inventario inventario = new Inventario();
        Item item = new Item();

        inventario.adicionarItem(item);

        assertTrue(inventario.getItens().contains(item));
    }

    @Test
    void limiteItensTeste() {
        Inventario inventario = new Inventario();

        inventario.adicionarItem(new Item());
        inventario.adicionarItem(new Item());
        inventario.adicionarItem(new Item());
        inventario.adicionarItem(new Item());

        assertEquals(3, inventario.getItens().size());
    }

    @Test
    void adicionarItemNuloTeste() {
        Inventario inventario = new Inventario();

        inventario.adicionarItem(null);

        assertTrue(inventario.getItens().isEmpty());
    }

    @Test
    void removerItemTeste() {
        Inventario inventario = new Inventario();
        Item item = new Item();

        inventario.adicionarItem(item);
        inventario.removerItem(item);

        assertTrue(inventario.getItens().isEmpty());
    }
}
