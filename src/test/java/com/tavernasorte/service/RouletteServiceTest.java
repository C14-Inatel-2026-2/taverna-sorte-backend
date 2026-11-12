package com.tavernasorte.service;

import com.tavernasorte.exception.InvalidBetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.random.RandomGenerator;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouletteServiceTest {

    @Mock
    private RandomGenerator random;

    private final RouletteService rouletteService = new RouletteService();

    private RouletteService serviceWithMock;

    @BeforeEach
    void setUp() {
        serviceWithMock = new RouletteService(random);
    }

    @Test
    void shouldSpinWithinRouletteRangeWithoutMock() {
        int result = rouletteService.spin();

        assertTrue(result >= 1 && result <= 10,
                "O resultado deve estar entre 1 e 10");
    }

    @ParameterizedTest
    @CsvSource({"0, 1", "9, 10"})
    void shouldConvertRandomValueToRouletteResult(int randomValue, int expectedResult) {
        when(random.nextInt(10)).thenReturn(randomValue);

        int result = serviceWithMock.spin();

        assertEquals(expectedResult, result);
        verify(random).nextInt(10);
    }

    @Test
    void shouldPayExactPrizeForWinningBet() {
        int number = 7;
        when(random.nextInt(10)).thenReturn(6);

        var result = serviceWithMock.play(number, new BigDecimal("0.10"));

        assertEquals(number, result.chosenNumber());
        assertEquals(number, result.drawnNumber());
        assertTrue(result.won());
        assertEquals(new BigDecimal("0.10"), result.betAmount());
        assertEquals(new BigDecimal("0.90"), result.prize());
        verify(random).nextInt(10);
    }

    @Test
    void shouldReturnZeroPrizeForLosingBet() {
        when(random.nextInt(10)).thenReturn(9);

        var result = serviceWithMock.play(1, BigDecimal.TEN);

        assertEquals(10, result.drawnNumber());
        assertEquals(false, result.won());
        assertEquals(new BigDecimal("0.00"), result.prize());
        assertEquals(new BigDecimal("10.00"), result.betAmount());
        verify(random).nextInt(10);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-1", "0.001", "10000.01"})
    void shouldRejectInvalidAmountBeforeDrawing(String amount) {
        BigDecimal bet = amount == null ? null : new BigDecimal(amount);

        assertThrows(InvalidBetException.class, () -> serviceWithMock.play(1, bet));

        verifyNoInteractions(random);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, 11})
    void shouldRejectInvalidNumberBeforeDrawing(Integer number) {
        assertThrows(InvalidBetException.class, () -> serviceWithMock.play(number, BigDecimal.ONE));

        verifyNoInteractions(random);
    }

    @Test
    void shouldAcceptMaximumBet() {
        when(random.nextInt(10)).thenReturn(0);

        var result = serviceWithMock.play(1, new BigDecimal("10000.00"));

        assertEquals(new BigDecimal("90000.00"), result.prize());
    }

    @Test
    void shouldPropagateFailureDuringPlay() {
        when(random.nextInt(10)).thenThrow(new IllegalStateException("Falha no sorteio"));

        assertThrows(IllegalStateException.class, () -> serviceWithMock.play(1, BigDecimal.ONE));

        verify(random).nextInt(10);
    }
}
