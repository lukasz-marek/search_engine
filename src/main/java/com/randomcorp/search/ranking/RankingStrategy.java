package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.Match;
import com.randomcorp.search.matching.Query;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class RankingStrategy {

    public final RankingResult rank(List<Match> matches, Query query) {
        final Optional<Integer> longestMatch = matches.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder());

        if (!longestMatch.isPresent()) {
            return new RankingResult(0);
        } else if (longestMatch.get() == query.getWords().size()) {
            return new RankingResult(100);
        }

        return computeRankingForPartialMatch(matches, query);
    }

    protected abstract RankingResult computeRankingForPartialMatch(List<Match> matches, Query query);

}
