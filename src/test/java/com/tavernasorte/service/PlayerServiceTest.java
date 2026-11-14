package com.tavernasorte.service;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.dto.PlayerResponse;
import com.tavernasorte.exception.DanceNotAllowedException;
import com.tavernasorte.exception.InvalidPlayerException;
import com.tavernasorte.exception.PlayerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void shouldReturnPlayerWhenItExists() {
        Player player = new Player("Chris Careca");

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerResponse response = playerService.findById(1L);

        assertNotNull(response);
    }

    @Test
    void shouldRejectLookupWhenPlayerDoesNotExist() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> playerService.findById(1L));
    }

    @Test
    void shouldCreatePlayerWithValidUsername() {
        Player player = new Player("Chris Careca");

        when(playerRepository.save(any(Player.class))).thenReturn(player);

        PlayerResponse response = playerService.create("Chris Careca");

        assertNotNull(response);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void shouldRejectDanceWhenPlayerIdIsNull() {
        assertThrows(InvalidPlayerException.class, () -> playerService.danceForGnome(null));

        verifyNoInteractions(playerRepository);
    }

    @Test
    void shouldRejectDanceWhenPlayerDoesNotExist() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> playerService.danceForGnome(1L));
    }

    @Test
    void shouldRejectDanceWhenPlayerHasBalance() {
        Player player = new Player("Chris Careca");
        player.addBalance(50);

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        assertThrows(DanceNotAllowedException.class, () -> playerService.danceForGnome(1L));

        assertEquals(50, player.getBalance());
    }

    @Test
    void shouldAdd100BalanceWhenPlayerHasNoBalance() {
        Player player = new Player("Chris Careca");

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        playerService.danceForGnome(1L);

        assertEquals(100, player.getBalance());
    }
}
