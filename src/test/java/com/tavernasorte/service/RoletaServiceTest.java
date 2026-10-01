package com.tavernasorte.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoletaServiceTest {

    @Mock
    private Random random;

    private final RoletaService roletaService = new RoletaService();

    @Test
    void girarSemMockDeveRetornarNumeroNoIntervaloDaRoleta() {
        int resultado = roletaService.girar();

        assertTrue(resultado >= 1 && resultado <= 10,
                "O resultado deve estar entre 1 e 10");
    }

    @Test
    void girarSemMockDeveManterResultadosValidosEmVariasJogadas() {
        for (int jogada = 0; jogada < 100; jogada++) {
            int resultado = roletaService.girar();
            assertTrue(resultado >= 1 && resultado <= 10,
                    "A jogada " + jogada + " deve estar entre 1 e 10");
        }
    }

    @Test
    void girarComMockDeveConverterMaiorValorPossivelParaDez() {
        RoletaService servicoComMock = new RoletaService(random);
        when(random.nextInt(10)).thenReturn(9);

        int resultado = servicoComMock.girar();

        assertEquals(10, resultado);
        verify(random).nextInt(10);
    }

    @Test
    void girarComMockDevePropagarFalhaDoGerador() {
        RoletaService servicoComMock = new RoletaService(random);
        when(random.nextInt(10)).thenThrow(new IllegalStateException("Falha no sorteio"));

        IllegalStateException erro = assertThrows(
                IllegalStateException.class,
                servicoComMock::girar
        );

        assertEquals("Falha no sorteio", erro.getMessage());
        verify(random).nextInt(10);
    }
}
