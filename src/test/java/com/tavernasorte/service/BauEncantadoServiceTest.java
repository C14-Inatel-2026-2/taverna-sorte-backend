package com.tavernasorte.service;

import com.tavernasorte.dto.ResultadoBau;
import com.tavernasorte.entity.Simbolo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BauEncantadoServiceTest {

    @Mock
    private SorteadorSimbolo sorteadorSimbolo;

    // --- Testes COM mock ---

    @Test
    void abrirComTresDragoesDevePagarMultiplicadorDoDragao() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.DRAGAO, Simbolo.DRAGAO, Simbolo.DRAGAO);
        BauEncantadoService service = new BauEncantadoService(sorteadorSimbolo);

        ResultadoBau resultado = service.abrir(10.0);

        assertEquals(List.of(Simbolo.DRAGAO, Simbolo.DRAGAO, Simbolo.DRAGAO), resultado.getSimbolos());
        assertEquals(200.0, resultado.getPremio());
        verify(sorteadorSimbolo, times(3)).sortear();
    }

    @Test
    void abrirComTresSimbolosDiferentesNaoDevePagarPremio() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.MOEDA, Simbolo.POCAO, Simbolo.ESPADA);
        BauEncantadoService service = new BauEncantadoService(sorteadorSimbolo);

        ResultadoBau resultado = service.abrir(10.0);

        assertEquals(0.0, resultado.getPremio());
    }

    // --- Testes SEM mock ---

    @Test
    void calcularPremioComDoisSimbolosIguaisDeveDevolverAposta() {
        BauEncantadoService service = new BauEncantadoService(new SorteadorSimboloAleatorio());

        double premio = service.calcularPremio(List.of(Simbolo.COROA, Simbolo.COROA, Simbolo.ESPADA), 10.0);

        assertEquals(10.0, premio);
    }

    @Test
    void abrirComValorZeroDeveLancarExcecao() {
        BauEncantadoService service = new BauEncantadoService(new SorteadorSimboloAleatorio());

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.abrir(0));

        assertEquals("O valor da aposta deve ser maior que zero", excecao.getMessage());
    }
}
