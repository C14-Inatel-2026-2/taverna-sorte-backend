package com.tavernasorte.service;

import com.tavernasorte.entity.Item;
import com.tavernasorte.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void itemExistePorBuscaIDTeste() {

        ItemService itemService = new ItemService(itemRepository);
        Item item = new Item();
        item.setNome("Poção");
        item.setPreco(5);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item resultado = itemService.buscarPorId(1L);
        assertNotNull(resultado);
    }
}
