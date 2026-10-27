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
    private ShopService shopService;

    @Test
    void shouldBuyItem() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(shopService.buyItem(player, 1L));
    }

    @Test
    void shouldDebitPlayerBalanceAfterPurchase() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 1L);

        assertEquals(50, player.getBalance());
    }

    @Test
    void shouldAddPurchasedItemToPlayerInventory() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 1L);

        assertTrue(player.getInventory().getItems().contains(item));
    }

    @Test
    void shouldRejectPurchaseForNullPlayer() {
        shopService = new ShopService(itemRepository);

        assertFalse(shopService.buyItem(null, 1L));
    }

    @Test
    void shouldRejectPurchaseWithNullItemId() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();

        assertFalse(shopService.buyItem(player, null));
    }

    @Test
    void shouldPreserveBalanceForNullItemId() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        shopService.buyItem(player, null);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldRejectPurchaseOfMissingItem() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertFalse(shopService.buyItem(player, 99L));
    }

    @Test
    void shouldPreserveBalanceForMissingItem() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        shopService.buyItem(player, 99L);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldKeepInventoryEmptyForMissingItem() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        shopService.buyItem(player, 99L);

        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldRejectPurchaseWithInsufficientBalance() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(20);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertFalse(shopService.buyItem(player, 1L));
    }

    @Test
    void shouldPreserveInsufficientBalance() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(20);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 1L);

        assertEquals(20, player.getBalance());
    }

    @Test
    void shouldKeepInventoryEmptyWithInsufficientBalance() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(20);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 1L);

        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldRejectPurchaseWithFullInventory() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(10);

        player.getInventory().addItem(item);
        player.getInventory().addItem(item);
        player.getInventory().addItem(item);

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        assertFalse(shopService.buyItem(player, 4L));
    }

    @Test
    void shouldPreserveBalanceWithFullInventory() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(10);

        player.getInventory().addItem(item);
        player.getInventory().addItem(item);
        player.getInventory().addItem(item);

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 4L);

        assertEquals(100, player.getBalance());
    }

    @Test
    void shouldPreserveFullInventoryAfterRejectedPurchase() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(100);

        Item item = new Item();
        item.setPrice(10);

        player.getInventory().addItem(item);
        player.getInventory().addItem(item);
        player.getInventory().addItem(item);

        when(itemRepository.findById(4L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 4L);

        assertEquals(3, player.getInventory().getItems().size());
    }

    @Test
    void shouldAllowPurchaseWithExactBalance() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(50);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertTrue(shopService.buyItem(player, 1L));
    }

    @Test
    void shouldSpendEntireBalanceOnExactPricePurchase() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(50);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 1L);

        assertEquals(0, player.getBalance());
    }

    @Test
    void shouldAddItemAfterExactBalancePurchase() {
        shopService = new ShopService(itemRepository);
        Player player = new Player();
        player.addBalance(50);

        Item item = new Item();
        item.setPrice(50);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        shopService.buyItem(player, 1L);

        assertTrue(player.getInventory().getItems().contains(item));
    }
}