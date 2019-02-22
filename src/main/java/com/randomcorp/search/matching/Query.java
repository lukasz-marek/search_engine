package com.randomcorp.search.matching;

import com.randomcorp.processing.vocabulary.Word;

import java.util.List;

public final class Query {

    private final List<Word> query;

    public Query(List<Word> words){
        this.query = words;
    }

    public List<Word> getWords() {
        return query;
    }
}
