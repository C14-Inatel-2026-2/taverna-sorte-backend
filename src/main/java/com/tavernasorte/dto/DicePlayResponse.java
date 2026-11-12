package com.tavernasorte.dto;

import com.tavernasorte.model.BetType;

public class DicePlayResponse {

    private final int die1;
    private final int die2;
    private final int sum;
    private final BetType betType;
    private final boolean won;
    private final int prize;
    private final int balance;
    private final Long consumedItemId;
    private final int itemMultiplier;

    public DicePlayResponse(int die1, int die2, int sum, BetType betType,
                            boolean won, int prize, int balance, Long consumedItemId, int itemMultiplier) {
        this.die1 = die1;
        this.die2 = die2;
        this.sum = sum;
        this.betType = betType;
        this.won = won;
        this.prize = prize;
        this.balance = balance;
        this.consumedItemId = consumedItemId;
        this.itemMultiplier = itemMultiplier;
    }

    public int getDie1() {
        return die1;
    }

    public int getDie2() {
        return die2;
    }

    public int getSum() {
        return sum;
    }

    public BetType getBetType() {
        return betType;
    }

    public boolean isWon() {
        return won;
    }

    public int getPrize() {
        return prize;
    }

    public int getBalance() {
        return balance;
    }

    public Long getConsumedItemId() {
        return consumedItemId;
    }

    public int getItemMultiplier() {
        return itemMultiplier;
    }
}
