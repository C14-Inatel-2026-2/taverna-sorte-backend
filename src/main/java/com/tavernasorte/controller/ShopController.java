package com.tavernasorte.controller;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.dto.ItemResponse;
import com.tavernasorte.service.ItemService;
import com.tavernasorte.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ItemService itemService;
    private final ShopService shopService;

    public ShopController(ItemService itemService, ShopService shopService) {
        this.itemService = itemService;
        this.shopService = shopService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getItems() {
        return ResponseEntity.ok(itemService.findAll().stream().map(ItemResponse::from).toList());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable Long id) {
        Item item = itemService.findById(id);

        return ResponseEntity.ok(ItemResponse.from(item));
    }

    @PostMapping("/purchase/{playerId}/{itemId}")
    public ResponseEntity<Void> buyItem(
            @PathVariable Long playerId,
            @PathVariable Long itemId) {

        shopService.buyItem(playerId, itemId);
        return ResponseEntity.ok().build();
    }
}
