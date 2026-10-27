package com.tavernasorte.entity;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private static final int LIMITE_ITENS = 3;

    private List<Item> itens = new ArrayList<>();

    public void adicionarItem(Item item) {

        if (item == null || itens.size() >= LIMITE_ITENS) {
            return;
        }

        itens.add(item);
    }

    public int getLimiteItens() {
        return LIMITE_ITENS;
    }

    public List<Item> getItens() {
        return List.copyOf(itens);
    }

    public void removerItem(Item item) {
        itens.remove(item);
    }

}
