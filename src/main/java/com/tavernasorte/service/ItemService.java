package com.tavernasorte.service;

import com.tavernasorte.entity.Item;
import com.tavernasorte.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item buscarPorId(Long id) {
        return itemRepository.findById(id).orElse(null);
    }
}
