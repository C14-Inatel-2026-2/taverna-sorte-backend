package com.tavernasorte.service;

import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.model.BetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiceService {

    public static final String MISSING_PLAYER = "O jogador é obrigatório";
    public static final String INSUFFICIENT_BALANCE = "Saldo insuficiente para a aposta";
    public static final String ITEM_NOT_IN_INVENTORY = "O item escolhido não pertence ao inventário do jogador";
    public static final String MAX_BALANCE_EXCEEDED = "O prêmio máximo desta aposta ultrapassaria o saldo máximo de "
            + Player.MAX_BALANCE + " moedas";

    private static final int NO_ITEM_MULTIPLIER = 1;

    private final DiceRoller diceRoller;
    private final PlayerRepository playerRepository;

    public DiceService(DiceRoller diceRoller, PlayerRepository playerRepository) {
        this.diceRoller = diceRoller;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public DicePlayResponse play(Long playerId, BetType betType, int betAmount, Long itemId) {
        Player player = findPlayer(playerId);
        Item item = findItemInInventory(player, itemId);
        int itemMultiplier = multiplierOf(item);
        ensureMaximumPrizeFitsBalanceLimit(player, betType, betAmount, itemMultiplier);

        if (!player.debit(betAmount)) {
            throw new InvalidBetException(INSUFFICIENT_BALANCE);
        }

        int die1 = diceRoller.roll();
        int die2 = diceRoller.roll();
        int sum = die1 + die2;
        boolean won = isWinningBet(betType, sum);
        int prize = won ? calculateWinningPrize(betType, betAmount, itemMultiplier) : 0;
        if (prize > 0) {
            player.addBalance(prize);
        }

        if (item != null) {
            player.getInventory().removeItem(item);
        }

        return new DicePlayResponse(die1, die2, sum, betType, won, prize,
                player.getBalance(), itemId, itemMultiplier);
    }

    private Player findPlayer(Long playerId) {
        if (playerId == null) {
            throw new InvalidBetException(MISSING_PLAYER);
        }
        return playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
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

    private int multiplierOf(Item item) {
        return item == null ? NO_ITEM_MULTIPLIER : item.prizeMultiplier();
    }

    private void ensureMaximumPrizeFitsBalanceLimit(Player player, BetType betType, int betAmount, int itemMultiplier) {
        long maximumPrize = (long) betAmount * betType.getMultiplier() * itemMultiplier;
        if ((long) player.getBalance() - betAmount + maximumPrize > Player.MAX_BALANCE) {
            throw new InvalidBetException(MAX_BALANCE_EXCEEDED);
        }
    }

    private boolean isWinningBet(BetType betType, int sum) {
        return switch (betType) {
            case LOW -> sum >= 2 && sum <= 6;
            case SEVEN -> sum == 7;
            case HIGH -> sum >= 8 && sum <= 12;
        };
    }

    private int calculateWinningPrize(BetType betType, int betAmount, int itemMultiplier) {
        return betAmount * betType.getMultiplier() * itemMultiplier;
    }
}
