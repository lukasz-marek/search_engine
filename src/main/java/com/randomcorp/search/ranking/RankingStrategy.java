package com.randomcorp.search.ranking;

import com.randomcorp.search.matching.Match;
import com.randomcorp.search.matching.Query;

import java.util.List;

public interface RankingStrategy {

    RankingResult rank(List<Match> matches, Query query);

}
