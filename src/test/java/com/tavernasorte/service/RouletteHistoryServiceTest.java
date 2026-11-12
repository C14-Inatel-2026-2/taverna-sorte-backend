package com.tavernasorte.service;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.entity.RouletteHistory;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.database.repository.RouletteHistoryRepository;
import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.model.RouletteBetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouletteHistoryServiceTest {

    @Mock private PlayerRepository players;
    @Mock private RouletteHistoryRepository history;
    @Mock private RouletteService roulette;
    private RouletteHistoryService service;

    @BeforeEach
    void setUp() {
        service = new RouletteHistoryService(players, history, roulette);
    }

    @Test
    void shouldSaveCompletedRoundWithoutChangingBalance() {
        Player player = new Player();
        player.addBalance(100);
        var result = new RoulettePlayResponse(RouletteBetType.RED, null, 10, 1, "RED", true, 20);
        when(players.findById(1L)).thenReturn(Optional.of(player));
        when(roulette.play(RouletteBetType.RED, null, 10)).thenReturn(result);

        assertEquals(result, service.play(1L, RouletteBetType.RED, null, 10));

        var saved = ArgumentCaptor.forClass(RouletteHistory.class);
        verify(history).save(saved.capture());
        assertSame(player, saved.getValue().getPlayer());
        assertEquals(result, saved.getValue().getResult());
        assertNotNull(saved.getValue().getPlayedAt());
        assertEquals(100, player.getBalance());
        verify(players, never()).save(any());
    }

    @Test
    void shouldNotRecordRejectedBet() {
        when(players.findById(1L)).thenReturn(Optional.of(new Player()));
        when(roulette.play(RouletteBetType.RED, null, 0)).thenThrow(new InvalidBetException("Aposta inválida"));

        assertThrows(InvalidBetException.class, () -> service.play(1L, RouletteBetType.RED, null, 0));

        verifyNoInteractions(history);
    }

    @Test
    void shouldRejectMissingPlayerBeforePlaying() {
        when(players.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RouletteHistoryService.PlayerNotFoundException.class,
                () -> service.play(99L, RouletteBetType.NUMBER, 1, 10));

        verifyNoInteractions(roulette, history);
    }

    @Test
    void shouldReturnOnlyRequestedPlayerHistory() {
        when(players.existsById(1L)).thenReturn(true);
        var result = new RoulettePlayResponse(RouletteBetType.NUMBER, 7, 10, 8, "BLACK", false, 0);
        Instant date = Instant.parse("2026-11-12T12:00:00Z");
        when(history.findByPlayer_IdOrderByPlayedAtDescIdDesc(1L))
                .thenReturn(List.of(new RouletteHistory(new Player(), result, date)));

        var entries = service.findByPlayer(1L);

        assertEquals(1, entries.size());
        assertEquals(result, entries.getFirst().result());
        assertEquals(date, entries.getFirst().playedAt());
        verify(history).findByPlayer_IdOrderByPlayedAtDescIdDesc(1L);
        verifyNoInteractions(roulette);
    }

    @Test
    void shouldReturnEmptyHistoryForPlayerWithoutRounds() {
        when(players.existsById(1L)).thenReturn(true);
        when(history.findByPlayer_IdOrderByPlayedAtDescIdDesc(1L)).thenReturn(List.of());

        assertTrue(service.findByPlayer(1L).isEmpty());
    }

    @Test
    void shouldNotReturnHistoryForMissingPlayer() {
        when(players.existsById(99L)).thenReturn(false);

        assertThrows(RouletteHistoryService.PlayerNotFoundException.class, () -> service.findByPlayer(99L));

        verifyNoInteractions(history);
    }

    @Test
    void shouldNotReportSuccessIfHistoryCannotBeSaved() {
        when(players.findById(1L)).thenReturn(Optional.of(new Player()));
        when(roulette.play(RouletteBetType.BLACK, null, 10))
                .thenReturn(new RoulettePlayResponse(RouletteBetType.BLACK, null, 10, 2, "BLACK", true, 20));
        when(history.save(any())).thenThrow(new IllegalStateException("Banco indisponível"));

        assertThrows(IllegalStateException.class, () -> service.play(1L, RouletteBetType.BLACK, null, 10));
    }
}
