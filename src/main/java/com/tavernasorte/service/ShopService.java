package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.ItemRepository;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.exception.InsufficientBalanceException;
import com.tavernasorte.exception.InvalidItemException;
import com.tavernasorte.exception.InvalidPlayerException;
import com.tavernasorte.exception.InventoryFullException;
import com.tavernasorte.exception.ItemNotFoundException;
import com.tavernasorte.exception.PlayerNotFoundException;
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
    public void buyItem(Long playerId, Long itemId) {
        if (playerId == null) {
            throw new InvalidPlayerException("O jogador é obrigatório");
        }
        if (itemId == null) {
            throw new InvalidItemException("O item é obrigatório");
        }

        Player player = playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

        if (item.getPrice() <= 0) {
            throw new InvalidItemException("O preço do item deve ser maior que zero");
        }

        if (player.getInventory().getItems().size()
                >= player.getInventory().getItemLimit()) {
            throw new InventoryFullException();
        }

        if (!player.debit(item.getPrice())) {
            throw new InsufficientBalanceException("Saldo insuficiente para a compra");
        }

        player.getInventory().addItem(item);
    }
}
