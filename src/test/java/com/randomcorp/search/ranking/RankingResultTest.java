package com.randomcorp.search.ranking;

import org.junit.Test;

public class RankingResultTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForValueAbove100() {
        final RankingResult result = new RankingResult(101);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForValueBelow0() {
        final RankingResult result = new RankingResult(-5);
    }

    @Test
    public void shouldAccept0() {
        final RankingResult result = new RankingResult(0);
    }

    @Test
    public void shouldAccept100() {
        final RankingResult result = new RankingResult(100);
    }

    @Test
    public void shouldAcceptAnyValueBetween0And100() {
        final RankingResult result = new RankingResult(7);
    }
}
