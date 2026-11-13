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
    @CsvSource({"COIN,30", "POTION,50", "SWORD,80", "CROWN,120", "DRAGON,200"})
    void shouldPaySymbolMultiplierForThreeMatchingSymbols(Symbol symbol, int expectedPrize) {
        when(symbolPicker.pick()).thenReturn(symbol, symbol, symbol);

        EnchantedChestResponse result = service.open(10);

        assertAll("resultado do baú",
                () -> assertEquals(List.of(symbol, symbol, symbol), result.getSymbols()),
                () -> assertEquals(expectedPrize, result.getPrize())
        );
        verify(symbolPicker, times(3)).pick();
    }

    @Test
    void shouldReturnBetForOnePair() {
        when(symbolPicker.pick()).thenReturn(Symbol.CROWN, Symbol.CROWN, Symbol.SWORD);

        EnchantedChestResponse result = service.open(10);

        assertEquals(10, result.getPrize());
    }

    @Test
    void shouldPayNothingForThreeDifferentSymbols() {
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(10);

        assertEquals(0, result.getPrize());
    }

    @ParameterizedTest
    @CsvSource({"1,3", "7,21", "333,999"})
    void shouldPayWholeCoinsForAnyValidBet(int bet, int expectedPrize) {
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        EnchantedChestResponse result = service.open(bet);

        assertEquals(expectedPrize, result.getPrize());
    }

    @Test
    void shouldCalculateExactMaximumPrize() {
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        EnchantedChestResponse result = service.open(ChestBetValue.MAXIMUM);

        assertEquals(200000, result.getPrize());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, Integer.MIN_VALUE})
    void shouldRejectNonPositiveAmountBeforePickingSymbols(int value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(value));

        assertEquals("O valor da aposta deve ser maior que zero", exception.getMessage());
        verifyNoInteractions(symbolPicker);
    }

    @ParameterizedTest
    @ValueSource(ints = {10001, 50000, Integer.MAX_VALUE})
    void shouldRejectAmountAboveMaximumBeforePickingSymbols(int value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(value));

        assertEquals(ChestBetValue.INVALID_MAXIMUM_VALUE, exception.getMessage());
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
