package com.randomcorp.search.ranking;

public final class RankingResult {

    private final int value;

    public RankingResult(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(String.format("Value must be between 0 and 100, %d was given.", value));
        }

        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
