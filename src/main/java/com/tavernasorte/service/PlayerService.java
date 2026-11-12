package com.tavernasorte.service;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findById(Long id) {
        return playerRepository.findById(id).orElse(null);
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