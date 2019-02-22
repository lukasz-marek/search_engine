package com.randomcorp.processing.vocabulary;

public final class Word {

    public String getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    private final String value;

    private final long id;

    public Word(long id, String value){
        this.id = id;
        this.value = value;
    }
}
