package com.tavernasorte.database;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void shouldStartWithZeroBalance() {
        Player player = new Player();
        assertEquals(0, player.getBalance());
    }

    @Test
    void shouldCreatePlayerWithEmptyInventory() {
        Player player = new Player();
        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldAddBalance() {
        Player player = new Player();

        player.addBalance(100);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldIgnoreNegativeBalanceAddition() {
        Player player = new Player();

        player.addBalance(-50);

        assertEquals(0, player.getBalance());
    }

    @Test
    void shouldDebitBalance() {
        Player player = new Player();
        player.addBalance(100);
        player.debit(30);

        assertEquals(70, player.getBalance());
    }

    @Test
    void shouldRejectDebitWithInsufficientBalance() {
        Player player = new Player();
        player.addBalance(20);
        player.debit(30);

        assertEquals(20, player.getBalance());
    }

    @Test
    void shouldKeepPlayerItemInInventory() {
        Player player = new Player();
        Item item = new Item();

        player.getInventory().addItem(item);

        assertTrue(player.getInventory().getItems().contains(item));
    }
}
