package com.tavernasorte.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RoletaServiceTest {

    private final RoletaService roletaService = new RoletaService();

    @Test
    void girarDeveRetornarNumeroNoIntervaloDaRoleta() {
        int resultado = roletaService.girar();

        assertTrue(resultado >= 1 && resultado <= 10,
                "O resultado deve estar entre 1 e 10");
    }

    @Test
    void girarDeveManterResultadosValidosEmVariasJogadas() {
        for (int jogada = 0; jogada < 100; jogada++) {
            int resultado = roletaService.girar();
            assertTrue(resultado >= 1 && resultado <= 10,
                    "A jogada " + jogada + " deve estar entre 1 e 10");
        }
    }
}
