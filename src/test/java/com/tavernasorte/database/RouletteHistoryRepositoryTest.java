package com.tavernasorte.database;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.entity.RouletteHistory;
import com.tavernasorte.database.repository.RouletteHistoryRepository;
import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.model.RouletteBetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RouletteHistoryRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private RouletteHistoryRepository repository;

    @Test
    void shouldPersistSnapshotsAndFilterPlayerInReverseChronologicalOrder() {
        Player first = persistPlayer("first");
        Player second = persistPlayer("second");
        Long playerId = entityManager.getId(first, Long.class);
        var win = new RoulettePlayResponse(RouletteBetType.RED, null, 10, 1, "RED", true, 20);
        var loss = new RoulettePlayResponse(RouletteBetType.NUMBER, 7, 10, 8, "BLACK", false, 0);
        Instant earlier = Instant.parse("2026-11-12T12:00:00Z");
        Instant later = earlier.plusSeconds(60);
        var oldEntry = entityManager.persist(new RouletteHistory(first, loss, earlier));
        var tiedFirst = entityManager.persist(new RouletteHistory(first, win, later));
        var tiedLast = entityManager.persist(new RouletteHistory(first, loss, later));
        entityManager.persist(new RouletteHistory(second, win, later.plusSeconds(60)));
        entityManager.flush();
        entityManager.clear();

        var records = repository.findByPlayer_IdOrderByPlayedAtDescIdDesc(playerId);

        assertEquals(3, records.size());
        assertEquals(tiedLast.getId(), records.get(0).getId());
        assertEquals(tiedFirst.getId(), records.get(1).getId());
        assertEquals(oldEntry.getId(), records.get(2).getId());
        assertEquals(loss, records.get(0).getResult());
        assertEquals(win, records.get(1).getResult());
        assertEquals(later, records.get(0).getPlayedAt());
    }

    private Player persistPlayer(String username) {
        return entityManager.persist(new Player(username));
    }
}
