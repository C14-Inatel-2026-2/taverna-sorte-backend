package com.tavernasorte.service;

import com.tavernasorte.model.Simbolo;
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
class SorteadorSimboloAleatorioTest {

    @Mock
    private Random random;

    @ParameterizedTest
    @EnumSource(Simbolo.class)
    void sortearDeveConverterOIndiceSorteadoNoSimboloCorrespondente(Simbolo esperado) {
        SorteadorSimboloAleatorio sorteador = new SorteadorSimboloAleatorio(random);
        when(random.nextInt(Simbolo.values().length)).thenReturn(esperado.ordinal());

        Simbolo sorteado = sorteador.sortear();

        assertEquals(esperado, sorteado);
    }

    @Test
    void sortearDeveUsarTodosOsSimbolosComoLimiteDoSorteio() {
        SorteadorSimboloAleatorio sorteador = new SorteadorSimboloAleatorio(random);
        when(random.nextInt(Simbolo.values().length)).thenReturn(0);

        sorteador.sortear();

        verify(random).nextInt(Simbolo.values().length);
    }

    @Test
    void sortearSemMockDeveRetornarUmSimboloValido() {
        SorteadorSimboloAleatorio sorteador = new SorteadorSimboloAleatorio();

        Simbolo sorteado = sorteador.sortear();

        assertNotNull(sorteado);
    }
}
