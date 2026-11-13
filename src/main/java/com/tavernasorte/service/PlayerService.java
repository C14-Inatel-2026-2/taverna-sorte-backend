package com.tavernasorte.service;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.PlayerResponse;
import com.tavernasorte.exception.DanceNotAllowedException;
import com.tavernasorte.exception.InvalidPlayerException;
import com.tavernasorte.exception.PlayerNotFoundException;
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
        return PlayerResponse.from(findPlayer(id));
    }

    @Transactional
    public PlayerResponse create(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidPlayerException("O username é obrigatório");
        }
        Player player = playerRepository.save(new Player(username.trim()));
        return PlayerResponse.from(player);
    }

    @Transactional
    public void danceForGnome(Long playerId) {
        Player player = findPlayer(playerId);
        if (player.getBalance() != 0) {
            throw new DanceNotAllowedException();
        }

        player.addBalance(100);
    }

    private Player findPlayer(Long playerId) {
        if (playerId == null) {
            throw new InvalidPlayerException("O jogador é obrigatório");
        }
        return playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
    }
}
