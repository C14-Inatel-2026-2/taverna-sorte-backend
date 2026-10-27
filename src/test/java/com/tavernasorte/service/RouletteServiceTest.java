package com.tavernasorte.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouletteServiceTest {

    @Mock
    private Random random;

    private final RouletteService rouletteService = new RouletteService();

    @Test
    void shouldSpinWithinRouletteRangeWithoutMock() {
        int result = rouletteService.spin();

        assertTrue(result >= 1 && result <= 10,
                "O resultado deve estar entre 1 e 10");
    }

    @Test
    void shouldKeepSpinsWithinRangeAcrossRounds() {
        for (int round = 0; round < 100; round++) {
            int result = rouletteService.spin();
            assertTrue(result >= 1 && result <= 10,
                    "A jogada " + round + " deve estar entre 1 e 10");
        }
    }

    @Test
    void shouldConvertLargestRandomValueToTen() {
        RouletteService serviceWithMock = new RouletteService(random);
        when(random.nextInt(10)).thenReturn(9);

        int result = serviceWithMock.spin();

        assertEquals(10, result);
        verify(random).nextInt(10);
    }

    @Test
    void shouldPropagateRandomGeneratorFailure() {
        RouletteService serviceWithMock = new RouletteService(random);
        when(random.nextInt(10)).thenThrow(new IllegalStateException("Falha no sorteio"));

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                serviceWithMock::spin
        );

        assertEquals("Falha no sorteio", error.getMessage());
        verify(random).nextInt(10);
    }
}
