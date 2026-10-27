package com.tavernasorte.service;

import com.tavernasorte.dto.BauEncantadoResponse;
import com.tavernasorte.exception.ApostaInvalidaException;
import com.tavernasorte.model.Simbolo;
import com.tavernasorte.model.ValorApostaBau;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BauEncantadoServiceTest {

    @Mock
    private SorteadorSimbolo sorteadorSimbolo;

    private BauEncantadoService service;

    @BeforeEach
    void setUp() {
        service = new BauEncantadoService(sorteadorSimbolo);
    }

    @ParameterizedTest
    @CsvSource({"MOEDA,30.00", "POCAO,50.00", "ESPADA,80.00", "COROA,120.00", "DRAGAO,200.00"})
    void abrirComTresSimbolosIguaisDevePagarMultiplicadorDoSimbolo(Simbolo simbolo, String premioEsperado) {
        when(sorteadorSimbolo.sortear()).thenReturn(simbolo, simbolo, simbolo);

        BauEncantadoResponse resultado = service.abrir(new BigDecimal("10.00"));

        assertAll("resultado do baú",
                () -> assertEquals(List.of(simbolo, simbolo, simbolo), resultado.getSimbolos()),
                () -> assertEquals(new BigDecimal(premioEsperado), resultado.getPremio())
        );
        verify(sorteadorSimbolo, times(3)).sortear();
    }

    @Test
    void abrirComUmParDeveDevolverAApostaComDuasCasasDecimais() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.COROA, Simbolo.COROA, Simbolo.ESPADA);

        BauEncantadoResponse resultado = service.abrir(new BigDecimal("10.0"));

        assertEquals(new BigDecimal("10.00"), resultado.getPremio());
    }

    @Test
    void abrirComTresSimbolosDiferentesNaoDevePagarPremio() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.MOEDA, Simbolo.POCAO, Simbolo.ESPADA);

        BauEncantadoResponse resultado = service.abrir(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("0.00"), resultado.getPremio());
    }

    @ParameterizedTest
    @CsvSource({"0.1,0.30", "0.01,0.03", "1,3.00", "1.0,3.00", "1.00,3.00"})
    void abrirDeveCalcularPremioDecimalExato(String valor, String premioEsperado) {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.MOEDA);

        BauEncantadoResponse resultado = service.abrir(new BigDecimal(valor));

        assertEquals(new BigDecimal(premioEsperado), resultado.getPremio());
    }

    @Test
    void abrirNoLimiteDeveCalcularOPremioMaximoExatamente() {
        when(sorteadorSimbolo.sortear()).thenReturn(Simbolo.DRAGAO);

        BauEncantadoResponse resultado = service.abrir(new BigDecimal("999999999999.99"));

        assertEquals(new BigDecimal("19999999999999.80"), resultado.getPremio());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-10"})
    void abrirComValorNaoPositivoDeveRejeitarAntesDoSorteio(String valor) {
        ApostaInvalidaException excecao = assertThrows(ApostaInvalidaException.class,
                () -> service.abrir(new BigDecimal(valor)));

        assertEquals("O valor da aposta deve ser maior que zero", excecao.getMessage());
        verifyNoInteractions(sorteadorSimbolo);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.001", "10.000", "999999999999.999", "1000000000000.00", "1e308"})
    void abrirComValorForaDosLimitesDeveRejeitarAntesDoSorteio(String valor) {
        ApostaInvalidaException excecao = assertThrows(ApostaInvalidaException.class,
                () -> service.abrir(new BigDecimal(valor)));

        assertEquals(ValorApostaBau.FORMATO_INVALIDO, excecao.getMessage());
        verifyNoInteractions(sorteadorSimbolo);
    }

    @Test
    void abrirSemValorDeveRejeitarAntesDoSorteio() {
        ApostaInvalidaException excecao = assertThrows(ApostaInvalidaException.class,
                () -> service.abrir(null));

        assertEquals("O valor da aposta é obrigatório", excecao.getMessage());
        verifyNoInteractions(sorteadorSimbolo);
    }
}
