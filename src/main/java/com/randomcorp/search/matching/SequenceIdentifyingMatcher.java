package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;

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
        for(int i = 0; i < query.getWords().size(); i++){
            final MatchingReport match = match(i, queriedIndexes, query);
            matchData.add(match);
        }

        return new SearchResult(matchData);
    }

    protected MatchingReport match(int startIndex, Map<Word, Set<Long>> queriedIndexes, Query query){
        final List<Set<Long>> matchingWords = new ArrayList<>();
        for(Word word : query.getWords().subList(startIndex, query.getWords().size())){
            matchingWords.add(new HashSet<>(queriedIndexes.get(word)));
        }

        int matchLength = 0;
        int matchSize = 0;
        for(int i = 0; i < matchingWords.size(); i++){
            final Set<Long> current = matchingWords.get(i);
            matchSize = current.size();

            if (current.isEmpty()){
                break;
            }

            if (i == matchingWords.size() - 1){
                matchLength += 1;
                break;
            }

            final Set<Long> next = matchingWords.get(i + 1);
            final Set<Long> successors = getSuccessors(current);
            next.retainAll(successors);
            matchLength += 1;

            if (next.isEmpty()){
                break;
            }
        }

        return new MatchingReport(matchLength, matchSize);
    }

    protected Set<Long> getSuccessors(Set<Long> currentPositions){
        final Set<Long> successors = new HashSet<>();
        for(long current : currentPositions){
            for(int i = 1; i<= MAX_GAP; i++){
                successors.add(current + i);
            }
        }
        return Collections.unmodifiableSet(successors);
    }

}
