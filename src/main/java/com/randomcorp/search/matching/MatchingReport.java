package com.randomcorp.search.matching;

public class MatchingReport {

    private final int maxLength;

    private final int numberOfSequences;

    public MatchingReport(int maxLength, int numberOfSequences) {
        this.maxLength = maxLength;
        this.numberOfSequences = numberOfSequences;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getNumberOfSequences() {
        return numberOfSequences;
    }
}
