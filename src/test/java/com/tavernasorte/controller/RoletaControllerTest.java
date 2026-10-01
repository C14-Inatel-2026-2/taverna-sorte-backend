package com.tavernasorte.controller;

import com.tavernasorte.service.RoletaService;
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
class RoletaControllerTest {

    @Mock
    private RoletaService roletaService;

    @InjectMocks
    private RoletaController roletaController;

    @Test
    void girarDeveRetornarResultadoDoServico() {
        when(roletaService.girar()).thenReturn(7);

        int resultado = roletaController.girar();

        assertEquals(7, resultado);
        verify(roletaService).girar();
    }

    @Test
    void girarDevePropagarFalhaDoServico() {
        IllegalStateException falha = new IllegalStateException("Falha ao girar");
        when(roletaService.girar()).thenThrow(falha);

        IllegalStateException erro = assertThrows(
                IllegalStateException.class,
                () -> roletaController.girar()
        );

        assertEquals("Falha ao girar", erro.getMessage());
        verify(roletaService).girar();
    }
}
