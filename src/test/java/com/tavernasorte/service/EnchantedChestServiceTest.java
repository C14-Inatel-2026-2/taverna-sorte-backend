package com.tavernasorte.service;

import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.model.Symbol;
import com.tavernasorte.model.ChestBetValue;
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
class EnchantedChestServiceTest {

    @Mock
    private SymbolPicker sorteadorSymbol;

    private EnchantedChestService service;

    @BeforeEach
    void setUp() {
        service = new EnchantedChestService(sorteadorSymbol);
    }

    @ParameterizedTest
    @CsvSource({"MOEDA,30.00", "POCAO,50.00", "ESPADA,80.00", "COROA,120.00", "DRAGAO,200.00"})
    void abrirComTresSymbolsIguaisDevePagarMultiplicadorDoSymbol(Symbol simbolo, String premioEsperado) {
        when(sorteadorSymbol.sortear()).thenReturn(simbolo, simbolo, simbolo);

        EnchantedChestResponse resultado = service.abrir(new BigDecimal("10.00"));

        assertAll("resultado do baú",
                () -> assertEquals(List.of(simbolo, simbolo, simbolo), resultado.getSymbols()),
                () -> assertEquals(new BigDecimal(premioEsperado), resultado.getPremio())
        );
        verify(sorteadorSymbol, times(3)).sortear();
    }

    @Test
    void abrirComUmParDeveDevolverAApostaComDuasCasasDecimais() {
        when(sorteadorSymbol.sortear()).thenReturn(Symbol.COROA, Symbol.COROA, Symbol.ESPADA);

        EnchantedChestResponse resultado = service.abrir(new BigDecimal("10.0"));

        assertEquals(new BigDecimal("10.00"), resultado.getPremio());
    }

    @Test
    void abrirComTresSymbolsDiferentesNaoDevePagarPremio() {
        when(sorteadorSymbol.sortear()).thenReturn(Symbol.MOEDA, Symbol.POCAO, Symbol.ESPADA);

        EnchantedChestResponse resultado = service.abrir(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("0.00"), resultado.getPremio());
    }

    @ParameterizedTest
    @CsvSource({"0.1,0.30", "0.01,0.03", "1,3.00", "1.0,3.00", "1.00,3.00"})
    void abrirDeveCalcularPremioDecimalExato(String valor, String premioEsperado) {
        when(sorteadorSymbol.sortear()).thenReturn(Symbol.MOEDA);

        EnchantedChestResponse resultado = service.abrir(new BigDecimal(valor));

        assertEquals(new BigDecimal(premioEsperado), resultado.getPremio());
    }

    @Test
    void abrirNoLimiteDeveCalcularOPremioMaximoExatamente() {
        when(sorteadorSymbol.sortear()).thenReturn(Symbol.DRAGAO);

        EnchantedChestResponse resultado = service.abrir(new BigDecimal("999999999999.99"));

        assertEquals(new BigDecimal("19999999999999.80"), resultado.getPremio());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-10"})
    void abrirComValorNaoPositivoDeveRejeitarAntesDoSorteio(String valor) {
        InvalidBetException excecao = assertThrows(InvalidBetException.class,
                () -> service.abrir(new BigDecimal(valor)));

        assertEquals("O valor da aposta deve ser maior que zero", excecao.getMessage());
        verifyNoInteractions(sorteadorSymbol);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.001", "10.000", "999999999999.999", "1000000000000.00", "1e308"})
    void abrirComValorForaDosLimitesDeveRejeitarAntesDoSorteio(String valor) {
        InvalidBetException excecao = assertThrows(InvalidBetException.class,
                () -> service.abrir(new BigDecimal(valor)));

        assertEquals(ChestBetValue.FORMATO_INVALIDO, excecao.getMessage());
        verifyNoInteractions(sorteadorSymbol);
    }

    @Test
    void abrirSemValorDeveRejeitarAntesDoSorteio() {
        InvalidBetException excecao = assertThrows(InvalidBetException.class,
                () -> service.abrir(null));

        assertEquals("O valor da aposta é obrigatório", excecao.getMessage());
        verifyNoInteractions(sorteadorSymbol);
    }
}
