package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    private final ItemRepository itemRepository;

    public ShopService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public boolean buyItem(Player player, Long itemId) {

        if (player == null || itemId == null) {
            return false;
        }

        Item item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            return false;
        }

        if (player.getBalance() < item.getPrice()) {
            return false;
        }

        if (player.getInventory().getItems().size()
                >= player.getInventory().getItemLimit()) {
            return false;
        }

        if (!player.debit(item.getPrice())) {
            return false;
        }

        player.getInventory().addItem(item);

        return true;
    }
}