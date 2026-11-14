package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.model.Symbol;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class EnchantedChestService {

    public static final String MISSING_PLAYER = "O jogador é obrigatório";
    public static final String INSUFFICIENT_BALANCE = "Saldo insuficiente para a aposta";
    public static final String ITEM_NOT_IN_INVENTORY = "O item não está no inventário do jogador";
    public static final String MAX_BALANCE_EXCEEDED = "O prêmio máximo desta aposta ultrapassaria o saldo máximo de "
            + Player.MAX_BALANCE + " moedas";

    private static final int SYMBOL_COUNT = 3;
    private static final int NO_ITEM_MULTIPLIER = 1;
    private static final int MAX_SYMBOL_MULTIPLIER = Arrays.stream(Symbol.values())
            .mapToInt(Symbol::getMultiplier)
            .max()
            .orElseThrow();

    private final SymbolPicker symbolPicker;
    private final PlayerRepository playerRepository;

    public EnchantedChestService(SymbolPicker symbolPicker, PlayerRepository playerRepository) {
        this.symbolPicker = symbolPicker;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public EnchantedChestResponse open(Long playerId, Integer betAmount, Long itemId) {
        ChestBetValue bet = new ChestBetValue(betAmount);
        Player player = findPlayer(playerId);
        Item item = findItemInInventory(player, itemId);
        int itemMultiplier = multiplierOf(item);
        ensureMaximumPrizeFitsBalanceLimit(player, bet, itemMultiplier);

        if (!player.debit(bet.value())) {
            throw new InvalidBetException(INSUFFICIENT_BALANCE);
        }

        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < SYMBOL_COUNT; i++) {
            symbols.add(symbolPicker.pick());
        }

        int prize = calculatePrize(symbols, bet) * itemMultiplier;
        if (prize > 0) {
            player.addBalance(prize);
        }

        if (item != null) {
            player.getInventory().removeItem(item);
        }

        return new EnchantedChestResponse(symbols, prize, player.getBalance());
    }

    private Player findPlayer(Long playerId) {
        if (playerId == null) {
            throw new InvalidBetException(MISSING_PLAYER);
        }
        return playerRepository.findById(playerId)
                .orElseThrow(PlayerNotFoundException::new);
    }

    private Item findItemInInventory(Player player, Long itemId) {
        if (itemId == null) {
            return null;
        }
        return player.getInventory().getItems().stream()
                .filter(item -> itemId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new InvalidBetException(ITEM_NOT_IN_INVENTORY));
    }

    private void ensureMaximumPrizeFitsBalanceLimit(Player player, ChestBetValue bet, int itemMultiplier) {
        long maximumPrize = (long) bet.value() * MAX_SYMBOL_MULTIPLIER * itemMultiplier;
        if ((long) player.getBalance() - bet.value() + maximumPrize > Player.MAX_BALANCE) {
            throw new InvalidBetException(MAX_BALANCE_EXCEEDED);
        }
    }

    private int multiplierOf(Item item) {
        return item == null ? NO_ITEM_MULTIPLIER : item.prizeMultiplier();
    }

    private int calculatePrize(List<Symbol> symbols, ChestBetValue bet) {
        int distinctSymbolCount = new HashSet<>(symbols).size();

        if (distinctSymbolCount == 1) {
            return bet.value() * symbols.get(0).getMultiplier();
        }
        if (distinctSymbolCount == 2) {
            return bet.value();
        }
        return 0;
    }
}
