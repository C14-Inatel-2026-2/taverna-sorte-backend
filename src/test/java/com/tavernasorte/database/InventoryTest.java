package com.tavernasorte.database;

import com.tavernasorte.database.entity.Inventory;
import com.tavernasorte.database.entity.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    void shouldExposeInventoryItemLimit(){
        Inventory inventory = new Inventory();
        assertEquals(3, inventory.getItemLimit());
    }

    @Test
    void shouldStartWithEmptyInventory() {
        Inventory inventory = new Inventory();
        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void shouldAddItem() {
        Inventory inventory = new Inventory();
        Item item = new Item();

        inventory.addItem(item);

        assertTrue(inventory.getItems().contains(item));
    }

    @Test
    void shouldEnforceItemLimit() {
        Inventory inventory = new Inventory();

        inventory.addItem(new Item());
        inventory.addItem(new Item());
        inventory.addItem(new Item());
        inventory.addItem(new Item());

        assertEquals(3, inventory.getItems().size());
    }

    @Test
    void shouldIgnoreNullItem() {
        Inventory inventory = new Inventory();

        inventory.addItem(null);

        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void shouldRemoveItem() {
        Inventory inventory = new Inventory();
        Item item = new Item();

        inventory.addItem(item);
        inventory.removeItem(item);

        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void shouldIgnoreMissingItemRemoval(){
        Inventory inventory = new Inventory();
        Item item = new Item();
        inventory.removeItem(item);

        assertEquals(0, inventory.getItems().size());
    }

}
