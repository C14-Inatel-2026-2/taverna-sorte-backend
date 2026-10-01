package com.tavernasorte.api.controller;

import com.tavernasorte.api.service.RoletaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoletaControleTest {

    @Mock
    private RoletaService roletaService;

    @InjectMocks
    private RoletaControle roletaControle;

    @Test
    void girarDeveRetornarResultadoDoServico() {
        when(roletaService.girar()).thenReturn(7);

        int resultado = roletaControle.girar();

        assertEquals(7, resultado);
        verify(roletaService).girar();
    }

    @Test
    void girarDevePropagarFalhaDoServico() {
        IllegalStateException falha = new IllegalStateException("Falha ao girar");
        when(roletaService.girar()).thenThrow(falha);

        IllegalStateException erro = assertThrows(
                IllegalStateException.class,
                () -> roletaControle.girar()
        );

        assertEquals("Falha ao girar", erro.getMessage());
        verify(roletaService).girar();
    }
}
