package com.tavernasorte.service;

import com.tavernasorte.entity.Item;
import com.tavernasorte.entity.Jogador;
import com.tavernasorte.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class LojaService {

    private final ItemRepository itemRepository;

    public LojaService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public boolean comprarItem(Jogador jogador, Long itemId) {

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

        if (jogador.getInventario().getItens().size()
                >= jogador.getInventario().getLimiteItens()) {
            return false;
        }

        if (!jogador.debitar(item.getPreco())) {
            return false;
        }

        jogador.getInventario().adicionarItem(item);

        return true;
    }
}