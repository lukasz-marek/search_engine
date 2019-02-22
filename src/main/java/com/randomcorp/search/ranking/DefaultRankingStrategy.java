package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.MatchingReport;
import com.randomcorp.search.matching.Query;
import com.randomcorp.search.matching.SearchResult;

import java.util.Comparator;
import java.util.Optional;

public class DefaultRankingStrategy implements RankingStrategy{

    @Override
    public RankingResult rank(SearchResult searchResult, Query query) {
        final Optional<Integer> longestMatch = searchResult.getMatches()
                .stream()
                .map(MatchingReport::getMaxLength)
                .max(Comparator.naturalOrder());

        if(!longestMatch.isPresent()){
            return new RankingResult(0);
        }else if (longestMatch.get() == query.getWords().size()){
            return new RankingResult(100);
        }

        return compute(searchResult, query);

    }

    protected RankingResult compute(SearchResult searchResult, Query query){
        final int longestMatch = searchResult.getMatches()
                .stream()
                .map(MatchingReport::getMaxLength)
                .max(Comparator.naturalOrder()).get();

        final double coverage = ((double) longestMatch) / ((double) query.getWords().size());
        final int rank = (int)(coverage * 100.0);
        return new RankingResult(rank);
    }
}
