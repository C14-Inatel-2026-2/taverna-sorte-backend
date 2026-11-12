package com.tavernasorte.dto;

import com.tavernasorte.database.entity.Player;

import java.util.List;

public record PlayerResponse(Long id, String username, int balance, InventoryResponse inventory) {

    public static PlayerResponse from(Player player) {
        var inventory = player.getInventory();
        var items = inventory.getItems().stream()
                .map(item -> new ItemResponse(item.getId(), item.getName(), item.getPrice(),
                        item.getMultiplier(), item.getDescription()))
                .toList();
        return new PlayerResponse(player.getId(), player.getUsername(), player.getBalance(),
                new InventoryResponse(items, inventory.getItemLimit()));
    }

    public record InventoryResponse(List<ItemResponse> items, int itemLimit) {
    }

    public record ItemResponse(Long id, String name, int price, int multiplier, String description) {
    }
}
