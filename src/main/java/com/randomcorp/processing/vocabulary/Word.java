package com.randomcorp.processing.vocabulary;

import java.io.Serializable;
import java.util.Objects;

public final class Word implements Serializable {

    public String getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    private final String value;

    private final long id;

    public Word(long id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.id).hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Word)) {
            return false;
        }

        final Word asWord = (Word) o;

        return Objects.equals(this.id, asWord.id) && Objects.equals(this.value, asWord.value);
    }
}
