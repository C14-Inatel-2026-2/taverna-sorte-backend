package com.tavernasorte.database.repository;

import com.tavernasorte.database.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}