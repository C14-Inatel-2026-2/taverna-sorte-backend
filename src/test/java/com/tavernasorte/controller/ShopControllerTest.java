package com.tavernasorte.controller;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.service.ItemService;
import com.tavernasorte.service.ShopService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ShopService shopService;

    @InjectMocks
    private ShopController shopController;

    @Test
    void shouldReturnAllItems() {
        List<Item> items = List.of(mock(Item.class));

        when(itemService.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = shopController.getItems();

        assertEquals(ResponseEntity.ok(items), response);
    }

    @Test
    void shouldReturnItemWhenItExists() {
        Item item = mock(Item.class);

        when(itemService.findById(1L)).thenReturn(item);
        ResponseEntity<Item> response = shopController.getItem(1L);

        assertEquals(ResponseEntity.ok(item), response);
    }

    @Test
    void shouldReturnNotFoundWhenItemDoesNotExist() {
        when(itemService.findById(1L)).thenReturn(null);
        ResponseEntity<Item> response = shopController.getItem(1L);

        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void shouldReturnOkWhenPurchaseSucceeds() {
        when(shopService.buyItem(1L, 1L)).thenReturn(true);
        ResponseEntity<Void> response = shopController.buyItem(1L, 1L);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void shouldReturnBadRequestWhenPurchaseFails() {
        when(shopService.buyItem(1L, 1L)).thenReturn(false);
        ResponseEntity<Void> response = shopController.buyItem(1L, 1L);

        assertEquals(ResponseEntity.badRequest().build(), response);
    }
}
