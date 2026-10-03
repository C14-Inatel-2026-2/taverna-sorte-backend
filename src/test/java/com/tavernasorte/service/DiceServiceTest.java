package com.tavernasorte.service;

import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.entity.TipoAposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiceServiceTest {

    @Mock
    private DiceRoller diceRoller;

    private DiceService diceService;

    @BeforeEach
    void setUp() {
        diceService = new DiceService(diceRoller);
    }

    @Test
    void playComDadosDoisETresDeveRetornarSomaCinco() {
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse resultado = diceService.play(TipoAposta.LOW, 10.0);

        assertEquals(5, resultado.getSoma());
    }

    @Test
    void playComApostaLowESomaCincoDeveVencer() {
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse resultado = diceService.play(TipoAposta.LOW, 10.0);

        assertTrue(resultado.isVenceu());
    }

    @Test
    void playComApostaLowESomaCincoDevePagarODobro() {
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse resultado = diceService.play(TipoAposta.LOW, 10.0);

        assertEquals(20.0, resultado.getPremio());
    }

    @Test
    void playComDadosTresEQuatroDeveRetornarSomaSete() {
        when(diceRoller.roll()).thenReturn(3, 4);

        DicePlayResponse resultado = diceService.play(TipoAposta.SEVEN, 10.0);

        assertEquals(7, resultado.getSoma());
    }

    @Test
    void playComApostaSevenESomaSeteDeveVencer() {
        when(diceRoller.roll()).thenReturn(3, 4);

        DicePlayResponse resultado = diceService.play(TipoAposta.SEVEN, 10.0);

        assertTrue(resultado.isVenceu());
    }

    @Test
    void playComApostaSevenESomaSeteDevePagarOQuadruplo() {
        when(diceRoller.roll()).thenReturn(3, 4);

        DicePlayResponse resultado = diceService.play(TipoAposta.SEVEN, 10.0);

        assertEquals(40.0, resultado.getPremio());
    }

    @Test
    void playComDadosCincoECincoDeveRetornarSomaDez() {
        when(diceRoller.roll()).thenReturn(5, 5);

        DicePlayResponse resultado = diceService.play(TipoAposta.HIGH, 10.0);

        assertEquals(10, resultado.getSoma());
    }

    @Test
    void playComApostaHighESomaDezDeveVencer() {
        when(diceRoller.roll()).thenReturn(5, 5);

        DicePlayResponse resultado = diceService.play(TipoAposta.HIGH, 10.0);

        assertTrue(resultado.isVenceu());
    }

    @Test
    void playComApostaHighESomaDezDevePagarODobro() {
        when(diceRoller.roll()).thenReturn(5, 5);

        DicePlayResponse resultado = diceService.play(TipoAposta.HIGH, 10.0);

        assertEquals(20.0, resultado.getPremio());
    }

    @Test
    void playSemMockComValorZeroDeveLancarExcecao() {
        DiceRoller dadoFixo = () -> 1;
        DiceService servicoSemMock = new DiceService(dadoFixo);

        assertThrows(IllegalArgumentException.class,
                () -> servicoSemMock.play(TipoAposta.LOW, 0.0));
    }


    @Test
    void playSemMockComApostaLowESomaDozeDevePerder() {
        DiceRoller dadoFixo = () -> 6;
        DiceService service = new DiceService(dadoFixo);

        DicePlayResponse resultado =
                service.play(TipoAposta.LOW, 10.0);

        assertEquals(0.0, resultado.getPremio());
    }
}
