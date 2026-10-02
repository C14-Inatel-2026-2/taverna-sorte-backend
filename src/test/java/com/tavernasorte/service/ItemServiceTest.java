package com.tavernasorte.service;

import com.tavernasorte.entity.Item;
import com.tavernasorte.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Test
    void itemExistePorBuscaIDTeste() {

        ItemService itemService = new ItemService(itemRepository);
        Item item = new Item();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item resultado = itemService.buscarPorId(1L);
        assertNotNull(resultado);
    }

    @Test
    void buscarNomePorIDTeste() {

        ItemService itemService = new ItemService(itemRepository);

        Item item = new Item();
        item.setNome("Poção");

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item resultado = itemService.buscarPorId(1L);

        assertEquals("Poção", resultado.getNome());
    }

    @Test
    void buscarPrecoPorIdTeste() {

        ItemService itemService = new ItemService(itemRepository);

        Item item = new Item();
        item.setPreco(5);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item resultado = itemService.buscarPorId(1L);

        assertEquals(5, resultado.getPreco());
    }

    @Test
    void itemNaoExistePorBuscaIDTeste() {

        ItemService itemService = new ItemService(itemRepository);

        when(itemRepository.findById(999L))
                .thenReturn(Optional.empty());

        Item resultado = itemService.buscarPorId(999L);

        assertNull(resultado);
    }
}
