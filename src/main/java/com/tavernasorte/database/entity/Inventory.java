package com.tavernasorte.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private static final int ITEM_LIMIT = 3;

    @JsonProperty("itens")
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) {

        if (item == null || items.size() >= ITEM_LIMIT) {
            return;
        }

        items.add(item);
    }

    @JsonProperty("limiteItens")
    public int getItemLimit() {
        return ITEM_LIMIT;
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

}
