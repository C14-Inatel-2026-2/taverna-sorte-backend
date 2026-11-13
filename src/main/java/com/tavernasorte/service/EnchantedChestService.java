package com.tavernasorte.service;

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
    public static final String MAX_BALANCE_EXCEEDED = "O prêmio máximo desta aposta ultrapassaria o saldo máximo de "
            + Player.MAX_BALANCE + " moedas";

    private static final int SYMBOL_COUNT = 3;
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

    /**
     * Valida tudo antes do sorteio; se algo falhar, nada é sorteado nem debitado.
     * As alterações no jogador são salvas pelo JPA ao fim da transação.
     */
    @Transactional
    public EnchantedChestResponse open(Long playerId, Integer betAmount) {
        ChestBetValue bet = new ChestBetValue(betAmount);
        Player player = findPlayer(playerId);
        ensureMaximumPrizeFitsBalanceLimit(player, bet);

        if (!player.debit(bet.value())) {
            throw new InvalidBetException(INSUFFICIENT_BALANCE);
        }

        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < SYMBOL_COUNT; i++) {
            symbols.add(symbolPicker.pick());
        }

        int prize = calculatePrize(symbols, bet);
        if (prize > 0) {
            player.addBalance(prize); // Player recusa crédito de valor zero
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

    /**
     * Mesma regra do dado: recusa a aposta se o maior prêmio possível
     * (três dragões) deixaria o saldo acima do máximo.
     */
    private void ensureMaximumPrizeFitsBalanceLimit(Player player, ChestBetValue bet) {
        long maximumPrize = (long) bet.value() * MAX_SYMBOL_MULTIPLIER;
        if (player.getBalance() - bet.value() + maximumPrize > Player.MAX_BALANCE) {
            throw new InvalidBetException(MAX_BALANCE_EXCEEDED);
        }
    }

    private int calculatePrize(List<Symbol> symbols, ChestBetValue bet) {
        int distinctSymbolCount = new HashSet<>(symbols).size();

        if (distinctSymbolCount == 1) {
            return bet.value() * symbols.get(0).getMultiplier();
        }
        if (distinctSymbolCount == 2) {
            return bet.value(); // par de símbolos iguais devolve a aposta
        }
        return 0;
    }
}
