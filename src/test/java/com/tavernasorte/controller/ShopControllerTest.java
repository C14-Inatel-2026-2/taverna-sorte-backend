package com.tavernasorte.controller;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.exception.InsufficientBalanceException;
import com.tavernasorte.exception.InventoryFullException;
import com.tavernasorte.exception.ItemNotFoundException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.service.ItemService;
import com.tavernasorte.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    private static final String ITEM_JSON = """
            {"id":1,"name":"Poção","price":50,"multiplier":2,"description":"Dobra o prêmio"}
            """;

    @Mock
    private ItemService itemService;

    @Mock
    private ShopService shopService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ShopController(itemService, shopService))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void shouldReturnAllItems() throws Exception {
        when(itemService.findAll()).thenReturn(List.of(createItem()));

        mockMvc.perform(get("/shop/items"))
                .andExpect(status().isOk())
                .andExpect(content().json("[" + ITEM_JSON + "]", true));
    }

    @Test
    void shouldReturnItemWhenItExists() throws Exception {
        when(itemService.findById(1L)).thenReturn(createItem());

        mockMvc.perform(get("/shop/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(ITEM_JSON, true));
    }

    @Test
    void shouldReturnNotFoundWhenItemDoesNotExist() throws Exception {
        when(itemService.findById(1L)).thenThrow(new ItemNotFoundException());

        mockMvc.perform(get("/shop/items/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item não encontrado"));
    }

    @Test
    void shouldReturnOkWhenPurchaseSucceeds() throws Exception {
        mockMvc.perform(post("/shop/purchase/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(shopService).buyItem(1L, 1L);
    }

    @Test
    void shouldReturnBadRequestWhenPurchaseFails() throws Exception {
        doThrow(new InsufficientBalanceException("Saldo insuficiente para a compra")).when(shopService).buyItem(1L, 1L);

        mockMvc.perform(post("/shop/purchase/1/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Saldo insuficiente para a compra"));
    }

    @Test
    void shouldReturnNotFoundWhenPurchasingForMissingPlayer() throws Exception {
        doThrow(new PlayerNotFoundException()).when(shopService).buyItem(1L, 1L);

        mockMvc.perform(post("/shop/purchase/1/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));
    }

    @Test
    void shouldReturnNotFoundWhenPurchasingMissingItem() throws Exception {
        doThrow(new ItemNotFoundException()).when(shopService).buyItem(1L, 1L);

        mockMvc.perform(post("/shop/purchase/1/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item não encontrado"));
    }

    @Test
    void shouldReturnBadRequestWhenInventoryIsFull() throws Exception {
        doThrow(new InventoryFullException()).when(shopService).buyItem(1L, 1L);

        mockMvc.perform(post("/shop/purchase/1/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O inventário está cheio"));
    }

    private Item createItem() {
        Item item = new Item();
        ReflectionTestUtils.setField(item, "id", 1L);
        item.setName("Poção");
        item.setPrice(50);
        item.setMultiplier(2);
        item.setDescription("Dobra o prêmio");
        return item;
    }
}
