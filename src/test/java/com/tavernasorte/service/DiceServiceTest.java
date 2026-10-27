package com.tavernasorte.service;

import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.model.BetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiceServiceTest {

    @Mock
    private DiceRoller diceRoller;

    private DiceService diceService;

    @BeforeEach
    void setUp() {
        diceService = new DiceService(diceRoller);
    }

    @Test
    void shouldReturnFiveForDiceTwoAndThree() {
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse result = diceService.play(BetType.LOW, 10.0);

        assertEquals(5, result.getSum());
    }

    @Test
    void shouldWinLowBetWithSumFive() {
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse result = diceService.play(BetType.LOW, 10.0);

        assertTrue(result.isWon());
    }

    @Test
    void shouldPayDoubleForWinningLowBet() {
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse result = diceService.play(BetType.LOW, 10.0);

        assertEquals(20.0, result.getPrize());
    }

    @Test
    void shouldReturnSevenForDiceThreeAndFour() {
        when(diceRoller.roll()).thenReturn(3, 4);

        DicePlayResponse result = diceService.play(BetType.SEVEN, 10.0);

        assertEquals(7, result.getSum());
    }

    @Test
    void shouldWinSevenBetWithSumSeven() {
        when(diceRoller.roll()).thenReturn(3, 4);

        DicePlayResponse result = diceService.play(BetType.SEVEN, 10.0);

        assertTrue(result.isWon());
    }

    @Test
    void shouldPayQuadrupleForWinningSevenBet() {
        when(diceRoller.roll()).thenReturn(3, 4);

        DicePlayResponse result = diceService.play(BetType.SEVEN, 10.0);

        assertEquals(40.0, result.getPrize());
    }

    @Test
    void shouldReturnTenForDiceFiveAndFive() {
        when(diceRoller.roll()).thenReturn(5, 5);

        DicePlayResponse result = diceService.play(BetType.HIGH, 10.0);

        assertEquals(10, result.getSum());
    }

    @Test
    void shouldWinHighBetWithSumTen() {
        when(diceRoller.roll()).thenReturn(5, 5);

        DicePlayResponse result = diceService.play(BetType.HIGH, 10.0);

        assertTrue(result.isWon());
    }

    @Test
    void shouldPayDoubleForWinningHighBet() {
        when(diceRoller.roll()).thenReturn(5, 5);

        DicePlayResponse result = diceService.play(BetType.HIGH, 10.0);

        assertEquals(20.0, result.getPrize());
    }

    @Test
    void shouldRejectZeroBetWithoutMock() {
        DiceRoller fixedDie = () -> 1;
        DiceService serviceWithoutMock = new DiceService(fixedDie);

        assertThrows(IllegalArgumentException.class,
                () -> serviceWithoutMock.play(BetType.LOW, 0.0));
    }


    @Test
    void shouldLoseLowBetWithSumTwelveWithoutMock() {
        DiceRoller fixedDie = () -> 6;
        DiceService service = new DiceService(fixedDie);

        DicePlayResponse result =
                service.play(BetType.LOW, 10.0);

        assertEquals(0.0, result.getPrize());
    }
}
