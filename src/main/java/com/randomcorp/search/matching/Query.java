package com.randomcorp.search.matching;

import com.randomcorp.processing.vocabulary.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Query {

    private final List<Word> query;

    public Query(List<Word> words) {
        this.query = Collections.unmodifiableList(new ArrayList<>(words));
    }

    public List<Word> getWords() {
        return query;
    }
}
