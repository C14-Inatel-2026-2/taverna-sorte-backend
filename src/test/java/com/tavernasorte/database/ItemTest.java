package com.tavernasorte.database;

import com.tavernasorte.database.entity.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    void shouldCreateItem() {
        Item item = new Item();
        assertNotNull(item);
    }

    @Test
    void shouldSetName() {
        Item item = new Item();
        item.setName("Poção");
        assertEquals("Poção", item.getName());
    }

    @Test
    void shouldSetPrice() {
        Item item = new Item();
        item.setPrice(5);
        assertEquals(5, item.getPrice());
    }

    @Test
    void shouldStartWithNullId() {
        Item item = new Item();
        assertNull(item.getId());
    }

    @Test
    void shouldSetMultiplier(){
        Item item = new Item();
        item.setMultiplier(2.0);
        assertEquals(2.0,item.getMultiplier());
    }

    @Test
    void shouldSetLuck(){
        Item item = new Item();
        item.setLuck(2.0);
        assertEquals(2.0, item.getLuck());
    }

    @Test
    void shouldSetDescription(){
        Item item = new Item();
        item.setDescription("Descrição de um item.");
        assertEquals("Descrição de um item.", item.getDescription());
    }
}
