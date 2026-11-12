package com.tavernasorte.model;

import java.util.Set;

public enum RouletteColor {
    RED, BLACK, GREEN;

    public static final int MIN_NUMBER = 0;
    public static final int MAX_NUMBER = 36;
    public static final int NUMBER_COUNT = MAX_NUMBER - MIN_NUMBER + 1;

    private static final Set<Integer> RED_NUMBERS = Set.of(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);

    public static RouletteColor fromNumber(int number) {
        if (number < MIN_NUMBER || number > MAX_NUMBER) {
            throw new IllegalArgumentException("O número deve estar entre 0 e 36");
        }
        if (number == 0) {
            return GREEN;
        }
        return RED_NUMBERS.contains(number) ? RED : BLACK;
    }
}
