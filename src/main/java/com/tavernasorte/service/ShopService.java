package com.tavernasorte.service;

import com.tavernasorte.entity.Item;
import com.tavernasorte.entity.Player;
import com.tavernasorte.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    private final ItemRepository itemRepository;

    public ShopService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public boolean comprarItem(Player jogador, Long itemId) {

        if (jogador == null || itemId == null) {
            return false;
        }

        Item item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            return false;
        }

        if (jogador.getSaldo() < item.getPreco()) {
            return false;
        }

        if (jogador.getInventory().getItens().size()
                >= jogador.getInventory().getLimiteItens()) {
            return false;
        }

        if (!jogador.debitar(item.getPreco())) {
            return false;
        }

        jogador.getInventory().adicionarItem(item);

        return true;
    }
}