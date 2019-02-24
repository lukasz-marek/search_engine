package com.randomcorp.search.matching;

import java.util.List;

public final class Match {

    private final List<Long> occurrences;

    public Match(List<Long> occurrences) {
        this.occurrences = occurrences;
    }

    public List<Long> getOccurrences() {
        return occurrences;
    }
}
