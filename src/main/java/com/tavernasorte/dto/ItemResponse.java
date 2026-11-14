package com.tavernasorte.dto;

import com.tavernasorte.database.entity.Item;

public record ItemResponse(Long id, String name, int price, int multiplier, String description) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getPrice(),
                item.getMultiplier(), item.getDescription());
    }
}
