package com.tavernasorte.item;

import com.tavernasorte.item.model.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTeste {

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
}
