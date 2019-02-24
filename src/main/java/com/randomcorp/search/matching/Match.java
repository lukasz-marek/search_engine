package com.randomcorp.search.matching;

import java.util.List;

public final class Match {

    private final List<Long> places;

    public Match(List<Long> places) {
        this.places = places;
    }

    public List<Long> getPlaces() {
        return places;
    }
}
