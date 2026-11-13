package com.tavernasorte.service;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.model.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnchantedChestServiceTest {

    private static final Long PLAYER_ID = 1L;
    private static final int INITIAL_BALANCE = 1000;

    @Mock
    private SymbolPicker symbolPicker;

    @Mock
    private PlayerRepository playerRepository;

    private EnchantedChestService service;

    @BeforeEach
    void setUp() {
        service = new EnchantedChestService(symbolPicker, playerRepository);
    }

    // ---------- Cálculo do prêmio ----------

    @ParameterizedTest
    @CsvSource({"COIN,30", "POTION,50", "SWORD,80", "CROWN,120", "DRAGON,200"})
    void shouldPaySymbolMultiplierForThreeMatchingSymbols(Symbol symbol, int expectedPrize) {
        givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(symbol, symbol, symbol);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10);

        assertAll("resultado do baú",
                () -> assertEquals(List.of(symbol, symbol, symbol), result.getSymbols()),
                () -> assertEquals(expectedPrize, result.getPrize())
        );
        verify(symbolPicker, times(3)).pick();
    }

    @Test
    void shouldReturnBetForOnePair() {
        givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.CROWN, Symbol.CROWN, Symbol.SWORD);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10);

        assertEquals(10, result.getPrize());
    }

    @Test
    void shouldPayNothingForThreeDifferentSymbols() {
        givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10);

        assertEquals(0, result.getPrize());
    }

    @Test
    void shouldCalculateExactMaximumPrize() {
        givenPlayerWithBalance(ChestBetValue.MAXIMUM);
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        EnchantedChestResponse result = service.open(PLAYER_ID, ChestBetValue.MAXIMUM);

        assertEquals(200000, result.getPrize());
    }

    // ---------- Saldo do jogador ----------

    @Test
    void shouldDebitBetAndCreditPrizeToPlayerBalance() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10);

        assertAll("saldo após a jogada",
                () -> assertEquals(1020, player.getBalance()),
                () -> assertEquals(1020, result.getBalance())
        );
    }

    @Test
    void shouldOnlyDebitBetWhenThereIsNoPrize() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10);

        assertEquals(990, player.getBalance());
        assertEquals(990, result.getBalance());
    }

    @Test
    void shouldAcceptBetEqualToBalance() {
        Player player = givenPlayerWithBalance(10);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        service.open(PLAYER_ID, 10);

        assertEquals(0, player.getBalance());
    }

    @Test
    void shouldRejectBetAboveBalanceBeforePickingSymbols() {
        Player player = givenPlayerWithBalance(9);

        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, 10));

        assertEquals(EnchantedChestService.INSUFFICIENT_BALANCE, exception.getMessage());
        assertEquals(9, player.getBalance());
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldAcceptBetWhoseMaximumPrizeReachesExactlyTheBalanceLimit() {
        // 999810 - 10 + (10 x 20) = 1000000: no limite, ainda é permitido
        Player player = givenPlayerWithBalance(Player.MAX_BALANCE - 190);
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10);

        assertEquals(Player.MAX_BALANCE, player.getBalance());
        assertEquals(Player.MAX_BALANCE, result.getBalance());
    }

    @Test
    void shouldRejectBetWhoseMaximumPrizeWouldExceedBalanceLimitBeforePickingSymbols() {
        // 999811 - 10 + (10 x 20) = 1000001: passaria do limite se saísse o dragão
        int balance = Player.MAX_BALANCE - 189;
        Player player = givenPlayerWithBalance(balance);

        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, 10));

        assertEquals(EnchantedChestService.MAX_BALANCE_EXCEEDED, exception.getMessage());
        assertEquals(balance, player.getBalance());
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldRejectMissingPlayerBeforePickingSymbols() {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(null, 10));

        assertEquals(EnchantedChestService.MISSING_PLAYER, exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @Test
    void shouldRejectUnknownPlayerBeforePickingSymbols() {
        when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class,
                () -> service.open(PLAYER_ID, 10));

        verifyNoInteractions(symbolPicker);
    }

    // ---------- Validação da aposta ----------

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, Integer.MIN_VALUE})
    void shouldRejectNonPositiveAmountBeforePickingSymbols(int value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, value));

        assertEquals("O valor da aposta deve ser maior que zero", exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {10001, 50000, Integer.MAX_VALUE})
    void shouldRejectAmountAboveMaximumBeforePickingSymbols(int value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, value));

        assertEquals(ChestBetValue.INVALID_MAXIMUM_VALUE, exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @Test
    void shouldRejectMissingAmountBeforePickingSymbols() {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, null));

        assertEquals("O valor da aposta é obrigatório", exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    // ---------- Auxiliares ----------

    private Player givenPlayerWithBalance(int balance) {
        Player player = new Player();
        player.addBalance(balance);
        when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(player));
        return player;
    }
}
