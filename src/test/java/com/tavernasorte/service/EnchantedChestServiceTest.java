package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnchantedChestServiceTest {

    private static final Long PLAYER_ID = 1L;
    private static final Long ITEM_ID = 3L;
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

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, null);

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

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, null);

        assertEquals(10, result.getPrize());
    }

    @Test
    void shouldPayNothingForThreeDifferentSymbols() {
        givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, null);

        assertEquals(0, result.getPrize());
    }

    @Test
    void shouldCalculateExactMaximumPrize() {
        givenPlayerWithBalance(ChestBetValue.MAXIMUM);
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        EnchantedChestResponse result = service.open(PLAYER_ID, ChestBetValue.MAXIMUM, null);

        assertEquals(200000, result.getPrize());
    }

    // ---------- Saldo do jogador ----------

    @Test
    void shouldDebitBetAndCreditPrizeToPlayerBalance() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, null);

        assertAll("saldo após a jogada",
                () -> assertEquals(1020, player.getBalance()),
                () -> assertEquals(1020, result.getBalance())
        );
    }

    @Test
    void shouldOnlyDebitBetWhenThereIsNoPrize() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, null);

        assertEquals(990, player.getBalance());
        assertEquals(990, result.getBalance());
    }

    @Test
    void shouldAcceptBetEqualToBalance() {
        Player player = givenPlayerWithBalance(10);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        service.open(PLAYER_ID, 10, null);

        assertEquals(0, player.getBalance());
    }

    @Test
    void shouldRejectBetAboveBalanceBeforePickingSymbols() {
        Player player = givenPlayerWithBalance(9);

        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, 10, null));

        assertEquals(EnchantedChestService.INSUFFICIENT_BALANCE, exception.getMessage());
        assertEquals(9, player.getBalance());
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldAcceptBetWhoseMaximumPrizeReachesExactlyTheBalanceLimit() {
        // 999810 - 10 + (10 x 20) = 1000000: no limite, ainda é permitido
        Player player = givenPlayerWithBalance(Player.MAX_BALANCE - 190);
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, null);

        assertEquals(Player.MAX_BALANCE, player.getBalance());
        assertEquals(Player.MAX_BALANCE, result.getBalance());
    }

    @Test
    void shouldRejectBetWhoseMaximumPrizeWouldExceedBalanceLimitBeforePickingSymbols() {
        // 999811 - 10 + (10 x 20) = 1000001: passaria do limite se saísse o dragão
        int balance = Player.MAX_BALANCE - 189;
        Player player = givenPlayerWithBalance(balance);

        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, 10, null));

        assertEquals(EnchantedChestService.MAX_BALANCE_EXCEEDED, exception.getMessage());
        assertEquals(balance, player.getBalance());
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldConsiderItemMultiplierInBalanceLimitBeforePickingSymbols() {
        // sem item: 990000 - 10 + 200 cabe; com item x100: 990000 - 10 + 20000 não cabe
        int balance = Player.MAX_BALANCE - 10000;
        Player player = givenPlayerWithBalance(balance);
        Item item = givenItemInInventory(player, ITEM_ID, 100);

        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, 10, ITEM_ID));

        assertEquals(EnchantedChestService.MAX_BALANCE_EXCEEDED, exception.getMessage());
        assertEquals(balance, player.getBalance());
        assertTrue(player.getInventory().getItems().contains(item));
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldRejectMissingPlayerBeforePickingSymbols() {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(null, 10, null));

        assertEquals(EnchantedChestService.MISSING_PLAYER, exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @Test
    void shouldRejectUnknownPlayerBeforePickingSymbols() {
        when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class,
                () -> service.open(PLAYER_ID, 10, null));

        verifyNoInteractions(symbolPicker);
    }

    // ---------- Item do inventário ----------

    @Test
    void shouldMultiplyPrizeByItemMultiplier() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        givenItemInInventory(player, ITEM_ID, 2);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, ITEM_ID);

        assertAll("prêmio com item",
                () -> assertEquals(60, result.getPrize()),
                () -> assertEquals(1050, player.getBalance())
        );
    }

    @Test
    void shouldConsumeItemAfterUse() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        Item item = givenItemInInventory(player, ITEM_ID, 2);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        service.open(PLAYER_ID, 10, ITEM_ID);

        assertFalse(player.getInventory().getItems().contains(item));
    }

    @Test
    void shouldConsumeItemEvenWithoutPrize() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        Item item = givenItemInInventory(player, ITEM_ID, 2);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        EnchantedChestResponse result = service.open(PLAYER_ID, 10, ITEM_ID);

        assertEquals(0, result.getPrize());
        assertFalse(player.getInventory().getItems().contains(item));
    }

    @Test
    void shouldKeepOtherItemsInInventory() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        givenItemInInventory(player, ITEM_ID, 2);
        Item otherItem = givenItemInInventory(player, 4L, 5);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        service.open(PLAYER_ID, 10, ITEM_ID);

        assertEquals(List.of(otherItem), player.getInventory().getItems());
    }

    @Test
    void shouldRejectItemOutsideInventoryBeforePickingSymbols() {
        Player player = givenPlayerWithBalance(INITIAL_BALANCE);
        givenItemInInventory(player, 4L, 5);

        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, 10, ITEM_ID));

        assertEquals(EnchantedChestService.ITEM_NOT_IN_INVENTORY, exception.getMessage());
        assertEquals(INITIAL_BALANCE, player.getBalance());
        assertTrue(player.getInventory().getItems().stream().anyMatch(i -> i.getId().equals(4L)));
        verifyNoInteractions(symbolPicker);
    }

    // ---------- Validação da aposta ----------

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, Integer.MIN_VALUE})
    void shouldRejectNonPositiveAmountBeforePickingSymbols(int value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, value, null));

        assertEquals("O valor da aposta deve ser maior que zero", exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {10001, 50000, Integer.MAX_VALUE})
    void shouldRejectAmountAboveMaximumBeforePickingSymbols(int value) {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, value, null));

        assertEquals(ChestBetValue.INVALID_MAXIMUM_VALUE, exception.getMessage());
        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @Test
    void shouldRejectMissingAmountBeforePickingSymbols() {
        InvalidBetException exception = assertThrows(InvalidBetException.class,
                () -> service.open(PLAYER_ID, null, null));

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

    private Item givenItemInInventory(Player player, Long itemId, int multiplier) {
        Item item = new Item();
        item.setMultiplier(multiplier);
        ReflectionTestUtils.setField(item, "id", itemId); // o id é gerado pelo banco e não tem setter
        player.getInventory().addItem(item);
        return item;
    }
}
