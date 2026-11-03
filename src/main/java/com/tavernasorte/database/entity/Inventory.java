package com.tavernasorte.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inventory {

    private static final int ITEM_LIMIT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonProperty("items")
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
