package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.Match;
import com.randomcorp.search.matching.Query;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DefaultRankingStrategy implements RankingStrategy {

    @Override
    public RankingResult rank(List<Match> matches, Query query) {
        final Optional<Integer> longestMatch = matches.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder());

        if (!longestMatch.isPresent()) {
            return new RankingResult(0);
        } else if (longestMatch.get() == query.getWords().size()) {
            return new RankingResult(100);
        }

        return compute(matches, query);

    }

    protected RankingResult compute(List<Match> matches, Query query) {
        final int longestMatch = matches.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get();

        final double coverage = ((double) longestMatch) / ((double) query.getWords().size());
        final int rank = (int) (coverage * 100.0);
        return new RankingResult(rank);
    }
}
