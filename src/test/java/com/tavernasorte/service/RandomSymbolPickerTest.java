package com.tavernasorte.service;

import com.tavernasorte.model.Symbol;
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

    @ParameterizedTest
    @EnumSource(Symbol.class)
    void sortearDeveConverterOIndiceSorteadoNoSymbolCorrespondente(Symbol esperado) {
        RandomSymbolPicker sorteador = new RandomSymbolPicker(random);
        when(random.nextInt(Symbol.values().length)).thenReturn(esperado.ordinal());

        Symbol sorteado = sorteador.sortear();

        assertEquals(esperado, sorteado);
    }

    @Test
    void sortearDeveUsarTodosOsSymbolsComoLimiteDoSorteio() {
        RandomSymbolPicker sorteador = new RandomSymbolPicker(random);
        when(random.nextInt(Symbol.values().length)).thenReturn(0);

        sorteador.sortear();

        verify(random).nextInt(Symbol.values().length);
    }

    @Test
    void sortearSemMockDeveRetornarUmSymbolValido() {
        RandomSymbolPicker sorteador = new RandomSymbolPicker();

        Symbol sorteado = sorteador.sortear();

        assertNotNull(sorteado);
    }
}
