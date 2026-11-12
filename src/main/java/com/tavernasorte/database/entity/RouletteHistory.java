package com.tavernasorte.database.entity;

import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.model.RouletteBetType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "roulette_history", indexes = {
        @Index(name = "idx_roulette_history_player_date", columnList = "player_id,played_at,id")
})
public class RouletteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false, updatable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private RouletteBetType betType;

    @Column(updatable = false)
    private Integer chosenNumber;

    @Column(nullable = false, updatable = false)
    private int betAmount;

    @Column(nullable = false, updatable = false)
    private int drawnNumber;

    @Column(nullable = false, updatable = false)
    private String color;

    @Column(nullable = false, updatable = false)
    private boolean won;

    @Column(nullable = false, updatable = false)
    private int prize;

    @Column(name = "played_at", nullable = false, updatable = false)
    private Instant playedAt;

    protected RouletteHistory() {
    }

    public RouletteHistory(Player player, RoulettePlayResponse result, Instant playedAt) {
        this.player = player;
        this.betType = result.betType();
        this.chosenNumber = result.chosenNumber();
        this.betAmount = result.betAmount();
        this.drawnNumber = result.drawnNumber();
        this.color = result.color();
        this.won = result.won();
        this.prize = result.prize();
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Instant getPlayedAt() {
        return playedAt;
    }

    public RoulettePlayResponse getResult() {
        return new RoulettePlayResponse(betType, chosenNumber, betAmount, drawnNumber, color, won, prize);
    }
}
