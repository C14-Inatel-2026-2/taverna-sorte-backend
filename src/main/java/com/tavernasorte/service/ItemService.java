package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }
}
