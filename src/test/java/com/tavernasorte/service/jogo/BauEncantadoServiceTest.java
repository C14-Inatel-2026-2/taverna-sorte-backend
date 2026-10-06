package com.tavernasorte.service.jogo;

import com.tavernasorte.dto.ResultadoBau;
import com.tavernasorte.exception.ApostaInvalidaException;
import com.tavernasorte.model.jogo.Resultado;
import com.tavernasorte.model.jogo.bau.DetalheBauEncantado;
import com.tavernasorte.model.jogo.bau.Simbolo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BauEncantadoServiceTest {

    private static final double DELTA = 0.0001;

    @Mock
    private SorteadorSimbolo sorteadorSimbolo;

    private BauEncantadoService service;

    @BeforeEach
    void setUp() {
        service = new BauEncantadoService(sorteadorSimbolo);
    }

    // --- abrir(valorAposta): caminho feliz ---

    @Test
    void abrirComTresDragoesDevePagarMultiplicadorDoDragao() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.DRAGAO, Simbolo.DRAGAO, Simbolo.DRAGAO);

        ResultadoBau resultado = service.abrir(10.0);

        assertAll("resultado do baú",
                () -> assertEquals(List.of(Simbolo.DRAGAO, Simbolo.DRAGAO, Simbolo.DRAGAO), resultado.getSimbolos()),
                () -> assertEquals(200.0, resultado.getPremio(), DELTA)
        );
        verify(sorteadorSimbolo, times(3)).sortear();
    }

    @Test
    void abrirComUmParDeveDevolverAAposta() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.COROA, Simbolo.COROA, Simbolo.ESPADA);

        ResultadoBau resultado = service.abrir(10.0);

        assertEquals(10.0, resultado.getPremio(), DELTA);
    }

    @Test
    void abrirComTresSimbolosDiferentesNaoDevePagarPremio() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.MOEDA, Simbolo.POCAO, Simbolo.ESPADA);

        ResultadoBau resultado = service.abrir(10.0);

        assertEquals(0.0, resultado.getPremio(), DELTA);
    }

    // --- abrir(valorAposta): apostas inválidas ---

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.01, -10.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void abrirComValorInvalidoDeveLancarApostaInvalidaException(double valorInvalido) {
        ApostaInvalidaException excecao = assertThrows(ApostaInvalidaException.class,
                () -> service.abrir(valorInvalido));

        assertEquals("O valor da aposta deve ser maior que zero", excecao.getMessage());
    }

    @Test
    void abrirComValorInvalidoNaoDeveSortearSimbolos() {
        assertThrows(ApostaInvalidaException.class, () -> service.abrir(0));

        verifyNoInteractions(sorteadorSimbolo);
    }

    // --- calcularPremio: regra de pagamento ---

    @ParameterizedTest
    @EnumSource(Simbolo.class)
    void calcularPremioComTresSimbolosIguaisDevePagarMultiplicadorDoSimbolo(Simbolo simbolo) {
        double premio = service.calcularPremio(List.of(simbolo, simbolo, simbolo), 10.0);

        assertEquals(10.0 * simbolo.getMultiplicador(), premio, DELTA);
    }

    @Test
    void calcularPremioComDoisSimbolosIguaisDeveDevolverAposta() {
        double premio = service.calcularPremio(List.of(Simbolo.COROA, Simbolo.COROA, Simbolo.ESPADA), 10.0);

        assertEquals(10.0, premio, DELTA);
    }

    // --- Contrato Jogo<Void> ---

    @ParameterizedTest
    @MethodSource("resultadosDasJogadas")
    void jogarSemEscolhaDeveRetornarOResultadoDosSimbolos(List<Simbolo> simbolos, double multiplicador) {
        when(sorteadorSimbolo.sortear()).thenReturn(simbolos.get(0), simbolos.get(1), simbolos.get(2));
        Jogo<Void> jogo = service;

        Resultado resultado = jogo.jogar(null);

        assertAll("resultado da jogada",
                () -> assertEquals(multiplicador, resultado.multiplicador(), DELTA),
                () -> assertEquals(new DetalheBauEncantado(simbolos), resultado.detalhe())
        );
        verify(sorteadorSimbolo, times(3)).sortear();
    }

    @Test
    void idDeveSerBauEncantado() {
        assertEquals("bau-encantado", service.id());
    }

    @Test
    void tipoEscolhaDeveSerVoidPoisOBauNaoExigeEscolha() {
        assertEquals(Void.class, service.tipoEscolha());
    }

    private static Stream<Arguments> resultadosDasJogadas() {
        return Stream.of(
                Arguments.of(List.of(Simbolo.DRAGAO, Simbolo.DRAGAO, Simbolo.DRAGAO), 20.0),
                Arguments.of(List.of(Simbolo.COROA, Simbolo.COROA, Simbolo.ESPADA), 1.0),
                Arguments.of(List.of(Simbolo.MOEDA, Simbolo.POCAO, Simbolo.ESPADA), 0.0)
        );
    }
}
