package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.repository.ItemRepository;
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
    void shouldFindExistingItemById() {

        ItemService itemService = new ItemService(itemRepository);
        Item item = new Item();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void shouldFindItemNameById() {

        ItemService itemService = new ItemService(itemRepository);

        Item item = new Item();
        item.setName("Poção");

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);

        assertEquals("Poção", result.getName());
    }

    @Test
    void shouldFindItemPriceById() {

        ItemService itemService = new ItemService(itemRepository);

        Item item = new Item();
        item.setPrice(5);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);

        assertEquals(5, result.getPrice());
    }

    @Test
    void shouldReturnNullForMissingItem() {

        ItemService itemService = new ItemService(itemRepository);

        when(itemRepository.findById(999L))
                .thenReturn(Optional.empty());

        Item result = itemService.findById(999L);

        assertNull(result);
    }
}
