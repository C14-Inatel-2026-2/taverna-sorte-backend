package com.tavernasorte.service;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.PlayerResponse;
import com.tavernasorte.exception.InvalidBetException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public PlayerResponse findById(Long id) {
        return playerRepository.findById(id).map(PlayerResponse::from).orElse(null);
    }

    @Transactional
    public PlayerResponse create(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidBetException("O username é obrigatório");
        }
        Player player = playerRepository.save(new Player(username.trim()));
        return PlayerResponse.from(player);
    }

    @Transactional
    public boolean danceForGnome(Long playerId) {
        if (playerId == null) {
            return false;
        }

        Player player = playerRepository.findById(playerId).orElse(null);

        if (player == null || player.getBalance() != 0) {
            return false;
        }

        player.addBalance(100);
        return true;
    }
}
