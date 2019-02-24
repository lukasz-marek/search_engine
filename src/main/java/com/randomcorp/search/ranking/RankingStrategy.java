package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.Query;
import com.randomcorp.search.matching.SearchResult;

import java.util.List;

public interface RankingStrategy {

    RankingResult rank(List<List<Long>> matches , Query query);

}
