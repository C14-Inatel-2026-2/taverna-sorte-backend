package com.tavernasorte.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouletteColorTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36})
    void shouldIdentifyRedNumbers(int number) {
        assertEquals(RouletteColor.RED, RouletteColor.fromNumber(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35})
    void shouldIdentifyBlackNumbers(int number) {
        assertEquals(RouletteColor.BLACK, RouletteColor.fromNumber(number));
    }

    @Test
    void shouldIdentifyZeroAsGreen() {
        assertEquals(RouletteColor.GREEN, RouletteColor.fromNumber(0));
    }

    @Test
    void shouldRejectNumbersOutsideWheel() {
        assertThrows(IllegalArgumentException.class, () -> RouletteColor.fromNumber(-1));
        assertThrows(IllegalArgumentException.class, () -> RouletteColor.fromNumber(37));
    }
}
