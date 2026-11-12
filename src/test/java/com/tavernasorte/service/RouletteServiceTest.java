package com.tavernasorte.service;

import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.model.RouletteBetType;
import com.tavernasorte.model.RouletteColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouletteServiceTest {

    @Mock
    private Random random;
    private RouletteService service;

    @BeforeEach
    void setUp() {
        service = new RouletteService(random);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 36})
    void shouldSpinAtBothEuropeanRouletteLimits(int number) {
        when(random.nextInt(37)).thenReturn(number);

        assertEquals(number, service.spin());

        verify(random).nextInt(37);
    }

    @Test
    void shouldSpinWithDefaultGenerator() {
        int result = new RouletteService().spin();

        assertTrue(result >= 0 && result <= 36);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NUMBER, 0, 0, GREEN, true, 3.60",
            "NUMBER, 36, 36, RED, true, 3.60",
            "NUMBER, 7, 8, BLACK, false, 0.00",
            "RED, null, 12, RED, true, 0.20",
            "RED, null, 11, BLACK, false, 0.00",
            "BLACK, null, 11, BLACK, true, 0.20",
            "BLACK, null, 12, RED, false, 0.00",
            "RED, null, 0, GREEN, false, 0.00",
            "BLACK, null, 0, GREEN, false, 0.00"
    }, nullValues = "null")
    void shouldResolveNumberAndColorBets(RouletteBetType type, Integer chosenNumber,
                                       int drawnNumber, RouletteColor color, boolean won,
                                       String prize) {
        when(random.nextInt(37)).thenReturn(drawnNumber);

        var result = service.play(type, chosenNumber, new BigDecimal("0.10"));

        assertEquals(type, result.bet().type());
        assertEquals(chosenNumber, result.bet().chosenNumber());
        assertEquals(new BigDecimal("0.10"), result.bet().amount());
        assertEquals(drawnNumber, result.drawnNumber());
        assertEquals(color, result.color());
        assertEquals(won, result.won());
        assertEquals(new BigDecimal(prize), result.prize());
        verify(random).nextInt(37);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-1", "0.001", "10000.01", "1E+308"})
    void shouldRejectInvalidAmountBeforeSpinning(String amount) {
        BigDecimal bet = amount == null ? null : new BigDecimal(amount);

        assertThrows(InvalidBetException.class,
                () -> service.play(RouletteBetType.NUMBER, 1, bet));

        verifyNoInteractions(random);
    }

    @ParameterizedTest
    @CsvSource(value = {"null, 1", "NUMBER, null", "NUMBER, -1", "NUMBER, 37",
            "RED, 1", "BLACK, 0"}, nullValues = "null")
    void shouldRejectInvalidSelectionBeforeSpinning(RouletteBetType type, Integer number) {
        assertThrows(InvalidBetException.class, () -> service.play(type, number, BigDecimal.ONE));

        verifyNoInteractions(random);
    }

    @Test
    void shouldNormalizeAmountAndPayMaximumNumberPrize() {
        when(random.nextInt(37)).thenReturn(0);

        var result = service.play(RouletteBetType.NUMBER, 0, new BigDecimal("10000"));

        assertEquals(new BigDecimal("10000.00"), result.bet().amount());
        assertEquals(new BigDecimal("360000.00"), result.prize());
    }

    @Test
    void shouldPropagateGeneratorFailure() {
        when(random.nextInt(37)).thenThrow(new IllegalStateException("Falha no sorteio"));

        assertThrows(IllegalStateException.class,
                () -> service.play(RouletteBetType.RED, null, BigDecimal.ONE));

        verify(random).nextInt(37);
    }
}
