package com.randomcorp.search.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Match {

    private final List<Long> occurrences;

    public Match(List<Long> occurrences) {
        this.occurrences = Collections.unmodifiableList(new ArrayList<>(occurrences));
    }

    public List<Long> getOccurrences() {
        return occurrences;
    }
}
