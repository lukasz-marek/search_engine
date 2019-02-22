package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.Query;
import com.randomcorp.search.matching.SearchResult;

public interface RankingStrategy {

    RankingResult rank(SearchResult searchResult, Query query);

}
