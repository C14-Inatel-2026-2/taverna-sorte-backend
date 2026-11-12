package com.tavernasorte.service;

import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.model.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

    @ParameterizedTest
    @CsvSource(value = {
            "NUMBER, 0, 0, GREEN, true, 360",
            "NUMBER, 36, 36, RED, true, 360",
            "NUMBER, 7, 8, BLACK, false, 0",
            "RED, null, 12, RED, true, 20",
            "RED, null, 11, BLACK, false, 0",
            "BLACK, null, 29, BLACK, true, 20",
            "BLACK, null, 30, RED, false, 0",
            "RED, null, 0, GREEN, false, 0",
            "BLACK, null, 0, GREEN, false, 0"
    }, nullValues = "null")
    void shouldResolveBetsWithIntegerCoins(RouletteBetType type, Integer chosenNumber,
                                          int drawnNumber, String color, boolean won, int prize) {
        when(random.nextInt(37)).thenReturn(drawnNumber);

        var result = service.play(type, chosenNumber, 10);

        assertEquals(type, result.betType());
        assertEquals(chosenNumber, result.chosenNumber());
        assertEquals(10, result.betAmount());
        assertEquals(drawnNumber, result.drawnNumber());
        assertEquals(color, result.color());
        assertEquals(won, result.won());
        assertEquals(prize, result.prize());
        verify(random).nextInt(37);
    }

    @ParameterizedTest
    @CsvSource(value = {"null, 1", "NUMBER, null", "NUMBER, -1", "NUMBER, 37",
            "RED, 1", "BLACK, 0"}, nullValues = "null")
    void shouldRejectInvalidSelectionBeforeSpinning(RouletteBetType type, Integer number) {
        assertThrows(InvalidBetException.class, () -> service.play(type, number, 10));

        verifyNoInteractions(random);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void shouldRejectNonPositiveCoinsBeforeSpinning(int amount) {
        assertThrows(InvalidBetException.class,
                () -> service.play(RouletteBetType.NUMBER, 1, amount));

        verifyNoInteractions(random);
    }

    @ParameterizedTest
    @EnumSource(RouletteBetType.class)
    void shouldRejectPrizeOverflowBeforeSpinning(RouletteBetType type) {
        Integer number = type == RouletteBetType.NUMBER ? 1 : null;
        int amount = Integer.MAX_VALUE / type.getMultiplier() + 1;

        assertThrows(InvalidBetException.class, () -> service.play(type, number, amount));

        verifyNoInteractions(random);
    }

    @ParameterizedTest
    @EnumSource(RouletteBetType.class)
    void shouldAcceptLargestSafeAmount(RouletteBetType type) {
        Integer number = type == RouletteBetType.NUMBER ? 1 : null;
        int drawnNumber = type == RouletteBetType.BLACK ? 2 : 1;
        int amount = Integer.MAX_VALUE / type.getMultiplier();
        when(random.nextInt(37)).thenReturn(drawnNumber);

        var result = service.play(type, number, amount);

        assertEquals(amount * type.getMultiplier(), result.prize());
    }

    @Test
    void shouldPropagateRandomGeneratorFailure() {
        when(random.nextInt(37)).thenThrow(new IllegalStateException("Falha no sorteio"));

        assertThrows(IllegalStateException.class,
                () -> service.play(RouletteBetType.RED, null, 10));

        verify(random).nextInt(37);
    }
}