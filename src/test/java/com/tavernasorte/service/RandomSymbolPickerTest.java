package com.tavernasorte.service;

import com.tavernasorte.model.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomSymbolPickerTest {

    @Mock
    private Random random;

    private RandomSymbolPicker picker;

    @BeforeEach
    void setUp() {
        picker = new RandomSymbolPicker(random);
    }

    @ParameterizedTest
    @EnumSource(Symbol.class)
    void shouldReturnSymbolAtRandomIndexUsingAllSymbolsAsBound(Symbol expected) {
        when(random.nextInt(Symbol.values().length)).thenReturn(expected.ordinal());

        Symbol picked = picker.pick();

        assertEquals(expected, picked);
        verify(random).nextInt(Symbol.values().length);
    }

    @Test
    void shouldPickValidSymbolWithoutMock() {
        RandomSymbolPicker defaultPicker = new RandomSymbolPicker();

        Symbol picked = defaultPicker.pick();

        assertNotNull(picked);
    }
}
