package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemTeste {
    @Test
    void itemCriado(){
        Item item = new Item();
        assertNotNull(item);
    }
}
