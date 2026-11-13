package com.tavernasorte.controller;

import com.tavernasorte.database.entity.Item;
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
    public ResponseEntity<List<Item>> getItems() {
        return ResponseEntity.ok(itemService.findAll());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @PostMapping("/purchase/{playerId}/{itemId}")
    public ResponseEntity<Void> buyItem(
            @PathVariable Long playerId,
            @PathVariable Long itemId) {

        shopService.buyItem(playerId, itemId);
        return ResponseEntity.ok().build();
    }
}
