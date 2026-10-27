package com.tavernasorte.database.entity;

import com.tavernasorte.entity.Inventory;
import com.tavernasorte.entity.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    void limiteDoInventoryTeste(){
        Inventory inventario = new Inventory();
        assertEquals(3, inventario.getLimiteItens());
    }

    @Test
    void inventarioVazioTeste() {
        Inventory inventario = new Inventory();
        assertTrue(inventario.getItens().isEmpty());
    }

    @Test
    void adicionarItemTeste() {
        Inventory inventario = new Inventory();
        Item item = new Item();

        inventario.adicionarItem(item);

        assertTrue(inventario.getItens().contains(item));
    }

    @Test
    void limiteItensTeste() {
        Inventory inventario = new Inventory();

        inventario.adicionarItem(new Item());
        inventario.adicionarItem(new Item());
        inventario.adicionarItem(new Item());
        inventario.adicionarItem(new Item());

        assertEquals(3, inventario.getItens().size());
    }

    @Test
    void adicionarItemNuloTeste() {
        Inventory inventario = new Inventory();

        inventario.adicionarItem(null);

        assertTrue(inventario.getItens().isEmpty());
    }

    @Test
    void removerItemTeste() {
        Inventory inventario = new Inventory();
        Item item = new Item();

        inventario.adicionarItem(item);
        inventario.removerItem(item);

        assertTrue(inventario.getItens().isEmpty());
    }

    @Test
    void removerItemInexistenteTeste(){
        Inventory inventario = new Inventory();
        Item item = new Item();
        inventario.removerItem(item);

        assertEquals(0, inventario.getItens().size());
    }

}
