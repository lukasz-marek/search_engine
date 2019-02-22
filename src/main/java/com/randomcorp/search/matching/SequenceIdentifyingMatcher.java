package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;
import com.randomcorp.search.ranking.RankingResult;

import java.util.*;


public class SequenceIdentifyingMatcher implements Matcher {

    public final int MAX_GAP = 3;

    @Override
    public SearchResult search(FileImage fileImage, Query query) {

        final Map<Word, Set<Long>> queriedIndexes = new HashMap<>();
        final Map<Word, Set<Long>> indexedWords = fileImage.getWordIndexes();

        for(Word word : query.getWords()){
            queriedIndexes.put(word, indexedWords.getOrDefault(word, Collections.emptySet()));
        }

        final List<MatchingReport> matchData = new ArrayList<>();
        for(int i = 0; i < query.getWords().size() - 1; i++){
            final MatchingReport match = match(i, queriedIndexes, query);
            matchData.add(match);
        }

        return new SearchResult(matchData);
    }

    private MatchingReport match(int startIndex, Map<Word, Set<Long>> queriedIndexes, Query query){
        final List<Set<Long>> matchingWords = new ArrayList<>();
        for(Word word : query.getWords().subList(startIndex, query.getWords().size())){
            matchingWords.add(new HashSet<>(queriedIndexes.get(word)));
        }

        int matchLength = 0;
        int matchSize = 0;
        for(int i = 0; i < matchingWords.size() - 1; i++){
            final Set<Long> current = matchingWords.get(i);
            final Set<Long> next = matchingWords.get(i + 1);
            matchSize = current.size();
            final Set<Long> successors = getSuccessors(current);
            next.retainAll(successors);


            if (next.isEmpty()){
                break;
            }
            matchLength += 1;
        }

        return new MatchingReport(matchLength, matchSize);
    }

    private Set<Long> getSuccessors(Set<Long> currentPositions){
        final Set<Long> successors = new HashSet<>();
        for(long current : currentPositions){
            for(int i = 1; i<= MAX_GAP; i++){
                successors.add(current + i);
            }
        }
        return Collections.unmodifiableSet(successors);
    }

}
