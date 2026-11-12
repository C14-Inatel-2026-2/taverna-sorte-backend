package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.model.BetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiceServiceTest {

    private static final Long PLAYER_ID = 1L;
    private static final int INITIAL_BALANCE = 100;
    private static final int BET_AMOUNT = 10;

    @Mock
    private DiceRoller diceRoller;

    @Mock
    private PlayerRepository playerRepository;

    private DiceService diceService;

    @BeforeEach
    void setUp() {
        diceService = new DiceService(diceRoller, playerRepository);
    }

    @ParameterizedTest
    @CsvSource({
        "LOW, 2, 3, 5, true, 20, 110",
        "LOW, 6, 6, 12, false, 0, 90",
        "SEVEN, 3, 4, 7, true, 40, 130",
        "SEVEN, 1, 1, 2, false, 0, 90",
        "HIGH, 5, 5, 10, true, 20, 110",
        "HIGH, 1, 1, 2, false, 0, 90"
    })
    void shouldCalculateDiceResultAndBalance(
        BetType betType,
        int die1,
        int die2,
        int expectedSum,
        boolean expectedWin,
        int expectedPrize,
        int expectedBalance
    ) {
        Player player = createPlayer(INITIAL_BALANCE);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));
        when(diceRoller.roll()).thenReturn(die1, die2);

        DicePlayResponse result = diceService.play(
            PLAYER_ID, betType, BET_AMOUNT, null
        );

        assertAll(
            () -> assertEquals(expectedSum, result.getSum()),
            () -> assertEquals(expectedWin, result.isWon()),
            () -> assertEquals(expectedPrize, result.getPrize()),
            () -> assertEquals(expectedBalance, result.getBalance()),
            () -> assertEquals(expectedBalance, player.getBalance())
        );
    }

    @Test
    void shouldMultiplyPrizeWhenUsingItem() {
        Player player = createPlayer(INITIAL_BALANCE);
        Item item = createItem(2L, 3);
        player.getInventory().addItem(item);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse result = diceService.play(
            PLAYER_ID, BetType.LOW, BET_AMOUNT, 2L
        );

        assertAll(
            () -> assertEquals(60, result.getPrize()),
            () -> assertEquals(150, player.getBalance())
        );
    }

    @Test
    void shouldConsumeItemAfterWinning() {
        Player player = createPlayer(INITIAL_BALANCE);
        Item item = createItem(2L, 3);
        player.getInventory().addItem(item);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));
        when(diceRoller.roll()).thenReturn(2, 3);

        diceService.play(PLAYER_ID, BetType.LOW, BET_AMOUNT, 2L);

        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldConsumeItemAfterLosing() {
        Player player = createPlayer(INITIAL_BALANCE);
        Item item = createItem(2L, 3);
        player.getInventory().addItem(item);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));
        when(diceRoller.roll()).thenReturn(6, 6);

        DicePlayResponse result = diceService.play(
            PLAYER_ID, BetType.LOW, BET_AMOUNT, 2L
        );

        assertAll(
            () -> assertEquals(0, result.getPrize()),
            () -> assertEquals(90, player.getBalance()),
            () -> assertTrue(player.getInventory().getItems().isEmpty())
        );
    }

    @Test
    void shouldNotConsumeItemWhenPlayingWithoutItem() {
        Player player = createPlayer(INITIAL_BALANCE);
        Item item = createItem(2L, 3);
        player.getInventory().addItem(item);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));
        when(diceRoller.roll()).thenReturn(2, 3);

        DicePlayResponse result = diceService.play(
            PLAYER_ID, BetType.LOW, BET_AMOUNT, null
        );

        assertAll(
            () -> assertEquals(20, result.getPrize()),
            () -> assertEquals(1, player.getInventory().getItems().size())
        );
    }

    @Test
    void shouldRejectBetWhenBalanceIsInsufficient() {
        Player player = createPlayer(5);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));

        assertThrows(
            InvalidBetException.class,
            () -> diceService.play(
                PLAYER_ID, BetType.LOW, BET_AMOUNT, null
            )
        );

        assertEquals(5, player.getBalance());
        verifyNoInteractions(diceRoller);
    }


    @Test
    void shouldRejectBetExceedingMaximumBalance() {
        Player player = createPlayer(Player.MAX_BALANCE - 10);

        when(playerRepository.findById(PLAYER_ID))
            .thenReturn(Optional.of(player));

        assertThrows(
            InvalidBetException.class,
            () -> diceService.play(
                PLAYER_ID, BetType.SEVEN, BET_AMOUNT, null
            )
        );

        assertEquals(Player.MAX_BALANCE - 10, player.getBalance());
        verifyNoInteractions(diceRoller);
    }

    private Player createPlayer(int balance) {
        Player player = new Player();

        if (balance > 0) {
            player.addBalance(balance);
        }

        return player;
    }

    private Item createItem(Long id, int multiplier) {
        Item item = new Item();

        ReflectionTestUtils.setField(item, "id", id);
        item.setMultiplier(multiplier);

        return item;
    }
}
