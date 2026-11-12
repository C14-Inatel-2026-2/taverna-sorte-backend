package com.tavernasorte.service;

import com.tavernasorte.database.entity.RouletteHistory;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.database.repository.RouletteHistoryRepository;
import com.tavernasorte.dto.RouletteHistoryResponse;
import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.model.RouletteBetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RouletteHistoryService {

    private final PlayerRepository players;
    private final RouletteHistoryRepository history;
    private final RouletteService roulette;

    public RouletteHistoryService(PlayerRepository players, RouletteHistoryRepository history,
                                 RouletteService roulette) {
        this.players = players;
        this.history = history;
        this.roulette = roulette;
    }

    @Transactional
    public RoulettePlayResponse play(Long playerId, RouletteBetType type, Integer number, int amount) {
        var player = players.findById(playerId).orElseThrow(PlayerNotFoundException::new);
        RoulettePlayResponse result = roulette.play(type, number, amount);
        history.save(new RouletteHistory(player, result, Instant.now()));
        return result;
    }

    @Transactional(readOnly = true)
    public List<RouletteHistoryResponse> findByPlayer(Long playerId) {
        if (!players.existsById(playerId)) {
            throw new PlayerNotFoundException();
        }
        return history.findByPlayer_IdOrderByPlayedAtDescIdDesc(playerId).stream()
                .map(entry -> new RouletteHistoryResponse(entry.getId(), entry.getPlayedAt(), entry.getResult()))
                .toList();
    }

    public static class PlayerNotFoundException extends RuntimeException {
        public PlayerNotFoundException() {
            super("Jogador não encontrado");
        }
    }
}
