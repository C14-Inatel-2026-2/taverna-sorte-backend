package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.ItemRepository;
import com.tavernasorte.database.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {

    private final ItemRepository itemRepository;
    private final PlayerRepository playerRepository;

    public ShopService(
            ItemRepository itemRepository,
            PlayerRepository playerRepository
    ) {
        this.itemRepository = itemRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public boolean buyItem(Long playerId, Long itemId) {

        if (playerId == null || itemId == null) {
            return false;
        }

        Player player = playerRepository.findById(playerId).orElse(null);
        Item item = itemRepository.findById(itemId).orElse(null);

        if (player == null || item == null) {
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