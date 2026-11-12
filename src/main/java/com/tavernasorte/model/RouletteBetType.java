package com.tavernasorte.model;

public enum RouletteBetType {
    NUMBER(36), RED(2), BLACK(2);

    private final int totalReturnMultiplier;

    RouletteBetType(int totalReturnMultiplier) {
        this.totalReturnMultiplier = totalReturnMultiplier;
    }

    public int getTotalReturnMultiplier() {
        return totalReturnMultiplier;
    }

    public boolean wins(Integer chosenNumber, int drawnNumber) {
        RouletteColor color = RouletteColor.fromNumber(drawnNumber);
        return switch (this) {
            case NUMBER -> Integer.valueOf(drawnNumber).equals(chosenNumber);
            case RED -> color == RouletteColor.RED;
            case BLACK -> color == RouletteColor.BLACK;
        };
    }
}
