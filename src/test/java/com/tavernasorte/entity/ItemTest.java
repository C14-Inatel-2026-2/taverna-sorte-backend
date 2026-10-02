package com.tavernasorte.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    void itemCriado() {
        Item item = new Item();
        assertNotNull(item);
    }

    @Test
    void setNomeTeste() {
        Item item = new Item();
        item.setNome("Poção");
        assertEquals("Poção", item.getNome());
    }

    @Test
    void setPrecoTeste() {
        Item item = new Item();
        item.setPreco(5);
        assertEquals(5, item.getPreco());
    }

    @Test
    void getIdTeste() {
        Item item = new Item();
        assertNull(item.getId());
    }

    @Test
    void setMultiplicadorTeste(){
        Item item = new Item();
        item.setMultiplicador(2.0);
        assertEquals(2.0,item.getMultiplicador());
    }

    @Test
    void setSorteTeste(){
        Item item = new Item();
        item.setSorte(2.0);
        assertEquals(2.0, item.getSorte());
    }

    @Test
    void setDescricaoTeste(){
        Item item = new Item();
        item.setDescricao("Descrição de um item.");
        assertEquals("Descrição de um item.", item.getDescricao());
    }
}
