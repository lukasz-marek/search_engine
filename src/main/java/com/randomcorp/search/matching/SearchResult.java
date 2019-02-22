package com.randomcorp.search.matching;

import java.util.List;

public final class SearchResult {

    private final List<MatchingReport> matches;

    public SearchResult(List<MatchingReport>matches){
        this.matches = matches;
    }

    public List<MatchingReport> getMatches() {
        return matches;
    }
}
