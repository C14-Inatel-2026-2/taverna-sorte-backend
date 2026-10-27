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
    private SymbolPicker symbolPicker;

    private EnchantedChestService service;

    @BeforeEach
    void setUp() {
        service = new EnchantedChestService(symbolPicker);
    }

    @ParameterizedTest
    @CsvSource({"COIN,30.00", "POTION,50.00", "SWORD,80.00", "CROWN,120.00", "DRAGON,200.00"})
    void shouldPaySymbolMultiplierForThreeMatchingSymbols(Symbol symbol, String expectedPrize) {
        when(symbolPicker.pick()).thenReturn(symbol, symbol, symbol);

        EnchantedChestResponse result = service.open(new BigDecimal("10.00"));

        assertAll("resultado do baú",
                () -> assertEquals(List.of(symbol, symbol, symbol), result.getSymbols()),
                () -> assertEquals(new BigDecimal(expectedPrize), result.getPrize())
        );
        verify(symbolPicker, times(3)).pick();
    }

    @Test
    void shouldReturnBetWithTwoDecimalPlacesForOnePair() {
        when(symbolPicker.pick()).thenReturn(Symbol.CROWN, Symbol.CROWN, Symbol.SWORD);

        EnchantedChestResponse result = service.open(new BigDecimal("10.0"));

        assertEquals(new BigDecimal("10.00"), result.getPrize());
    }

    @Test
    void shouldPayNothingForThreeDifferentSymbols() {
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("0.00"), result.getPrize());
    }

    @ParameterizedTest
    @CsvSource({"0.1,0.30", "0.01,0.03", "1,3.00", "1.0,3.00", "1.00,3.00"})
    void shouldCalculateExactDecimalPrize(String value, String expectedPrize) {
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        EnchantedChestResponse result = service.open(new BigDecimal(value));

        assertEquals(new BigDecimal(expectedPrize), result.getPrize());
    }

    @Test
    void shouldCalculateExactMaximumPrize() {
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        EnchantedChestResponse result = service.open(new BigDecimal("999999999999.99"));

        assertEquals(new BigDecimal("19999999999999.80"), result.getPrize());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-10"})
    void shouldRejectNonPositiveAmountBeforePickingSymbols(String value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(new BigDecimal(value)));

        assertEquals("O valor da aposta deve ser maior que zero", exception.getMessage());
        verifyNoInteractions(symbolPicker);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.001", "10.000", "999999999999.999", "1000000000000.00", "1e308"})
    void shouldRejectOutOfRangeAmountBeforePickingSymbols(String value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(new BigDecimal(value)));

        assertEquals(ChestBetValue.INVALID_FORMAT, exception.getMessage());
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldRejectMissingAmountBeforePickingSymbols() {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(null));

        assertEquals("O valor da aposta é obrigatório", exception.getMessage());
        verifyNoInteractions(symbolPicker);
    }
}
