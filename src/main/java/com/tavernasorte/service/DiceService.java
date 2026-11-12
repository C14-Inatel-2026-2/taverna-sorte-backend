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

    private final DiceRoller diceRoller;
    private final PlayerRepository playerRepository;

    public DiceService(DiceRoller diceRoller, PlayerRepository playerRepository) {
        this.diceRoller = diceRoller;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public DicePlayResponse play(Long playerId, BetType betType, int betAmount, Long itemId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(PlayerNotFoundException::new);

        Item selectedItem = null;
        if (itemId != null) {
            selectedItem = player.getInventory().getItems().stream()
                    .filter(item -> itemId.equals(item.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidBetException("O item escolhido não pertence ao inventário do jogador"));
        }

        int itemMultiplier = selectedItem == null ? 1 : selectedItem.getMultiplier();

        int winningPrize = calculateWinningPrize(betType, betAmount, itemMultiplier);
        if (player.getBalance() - betAmount + winningPrize > Player.MAX_BALANCE) {
            throw new InvalidBetException("O prêmio máximo desta aposta ultrapassaria o saldo máximo de 1000000 moedas");
        }

        if (!player.debit(betAmount)) {
            throw new InvalidBetException("Saldo insuficiente para a aposta");
        }

        if (selectedItem != null) {
            // Consome uma unidade do inventário; o produto continua no catálogo.
            player.getInventory().removeItem(selectedItem);
        }

        int die1 = diceRoller.roll();
        int die2 = diceRoller.roll();
        int sum = die1 + die2;
        boolean won = isWinningBet(betType, sum);
        int prize = won ? winningPrize : 0;
        if (prize > 0) {
            player.addBalance(prize);
        }

        return new DicePlayResponse(die1, die2, sum, betType, won, prize,
                player.getBalance(), itemId, itemMultiplier);
    }

    private boolean isWinningBet(BetType betType, int sum) {
        return switch (betType) {
            case LOW -> sum >= 2 && sum <= 6;
            case SEVEN -> sum == 7;
            case HIGH -> sum >= 8 && sum <= 12;
        };
    }

    private int calculateWinningPrize(BetType betType, int betAmount, int itemMultiplier) {
        int gameMultiplier = betType == BetType.SEVEN ? 4 : 2;
        return betAmount * gameMultiplier * itemMultiplier;
    }
}
