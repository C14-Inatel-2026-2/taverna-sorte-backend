package com.tavernasorte.entity;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private final int limiteItens = 3;

    private List<Item> itens = new ArrayList<>();

    public boolean adicionarItem(Item item) {

        if (itens.size() >= limiteItens) {
            return false;
        }

        itens.add(item);
        return true;
    }
}
