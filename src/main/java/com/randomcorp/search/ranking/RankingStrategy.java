package com.randomcorp.search.ranking;

import com.randomcorp.search.Query;
import com.randomcorp.search.SearchResult;

public interface RankingStrategy {

    RankingResult rank(SearchResult searchResult, Query query);

}
