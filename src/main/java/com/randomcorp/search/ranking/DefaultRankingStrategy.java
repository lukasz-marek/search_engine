package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.Match;
import com.randomcorp.search.matching.Query;

import java.util.Comparator;
import java.util.List;

public final class DefaultRankingStrategy extends RankingStrategy {

    protected final RankingResult computeRankingForPartialMatch(List<Match> matches, Query query) {
        final int longestMatch = matches.stream()
                .map(Match::getOccurrences)
                .map(List::size)
                .max(Comparator.naturalOrder())
                .get();

        final double coverage = ((double) longestMatch) / ((double) query.getWords().size());
        final int rank = (int) (coverage * 100.0);
        return new RankingResult(rank);
    }
}
