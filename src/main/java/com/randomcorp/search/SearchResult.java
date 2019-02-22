package com.randomcorp.search;

import com.randomcorp.processing.vocabulary.Word;

import java.util.List;
import java.util.Map;

public final class SearchResult {

    private final Map<Integer, List<List<Word>>> matches;

    public SearchResult(Map<Integer, List<List<Word>>> matches){
        this.matches = matches;
    }

    public Map<Integer, List<List<Word>>> getMatches() {
        return matches;
    }
}
