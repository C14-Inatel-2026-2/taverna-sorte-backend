package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.ItemRepository;
import com.tavernasorte.database.repository.PlayerRepository;
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

    @Mock
    private PlayerRepository playerRepository;

    private ShopService shopService;

    @Test
    void shouldBuyItem() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(shopService.buyItem(1L, 1L));
    }

    @Test
    void shouldDebitPlayerBalanceAfterPurchase() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 1L);

        assertEquals(50, player.getBalance());
    }

    @Test
    void shouldAddPurchasedItemToPlayerInventory() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 1L);

        assertTrue(player.getInventory().getItems().contains(item));
    }

    @Test
    void shouldRejectPurchaseWithNullPlayerId() {
        shopService = new ShopService(itemRepository, playerRepository);

        assertFalse(shopService.buyItem(null, 1L));
    }

    @Test
    void shouldRejectPurchaseWithNullItemId() {
        shopService = new ShopService(itemRepository, playerRepository);

        assertFalse(shopService.buyItem(1L, null));
    }

    @Test
    void shouldPreserveBalanceForNullItemId() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        shopService.buyItem(1L, null);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldRejectPurchaseForMissingPlayer() {
        shopService = new ShopService(itemRepository, playerRepository);

        when(playerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertFalse(shopService.buyItem(99L, 1L));
    }

    @Test
    void shouldRejectPurchaseOfMissingItem() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertFalse(shopService.buyItem(1L, 99L));
    }

    @Test
    void shouldPreserveBalanceForMissingItem() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        shopService.buyItem(1L, 99L);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldKeepInventoryEmptyForMissingItem() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        shopService.buyItem(1L, 99L);

        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldRejectPurchaseWithInsufficientBalance() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(20);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertFalse(shopService.buyItem(1L, 1L));
    }

    @Test
    void shouldPreserveInsufficientBalance() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(20);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 1L);

        assertEquals(20, player.getBalance());
    }

    @Test
    void shouldKeepInventoryEmptyWithInsufficientBalance() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(20);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 1L);

        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldRejectPurchaseWithFullInventory() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(10);

        player.getInventory().addItem(item);
        player.getInventory().addItem(item);
        player.getInventory().addItem(item);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        assertFalse(shopService.buyItem(1L, 4L));
    }

    @Test
    void shouldPreserveBalanceWithFullInventory() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(10);

        player.getInventory().addItem(item);
        player.getInventory().addItem(item);
        player.getInventory().addItem(item);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 4L);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldPreserveFullInventoryAfterRejectedPurchase() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(10);

        player.getInventory().addItem(item);
        player.getInventory().addItem(item);
        player.getInventory().addItem(item);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 4L);

        assertEquals(3, player.getInventory().getItems().size());
    }

    @Test
    void shouldAllowPurchaseWithExactBalance() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(50);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(shopService.buyItem(1L, 1L));
    }

    @Test
    void shouldSpendEntireBalanceOnExactPricePurchase() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(50);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 1L);

        assertEquals(0, player.getBalance());
    }

    @Test
    void shouldAddItemAfterExactBalancePurchase() {
        shopService = new ShopService(itemRepository, playerRepository);
        Player player = new Player();
        player.addBalance(50);

        Item item = new Item();
        item.setPrice(50);

        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(1L, 1L);

        assertTrue(player.getInventory().getItems().contains(item));
    }
}