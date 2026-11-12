package com.tavernasorte.database.repository;

import com.tavernasorte.database.entity.RouletteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouletteHistoryRepository extends JpaRepository<RouletteHistory, Long> {

    List<RouletteHistory> findByPlayer_IdOrderByPlayedAtDescIdDesc(Long playerId);
}
