package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.repository.ItemRepository;
import com.tavernasorte.exception.InvalidItemException;
import com.tavernasorte.exception.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item findById(Long id) {
        if (id == null) {
            throw new InvalidItemException("O item é obrigatório");
        }
        return itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }
}
