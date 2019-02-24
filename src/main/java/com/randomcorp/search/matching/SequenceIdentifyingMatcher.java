package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;

import java.util.*;


public class SequenceIdentifyingMatcher implements Matcher {

    private final int MAX_GAP = 3;

    @Override
    public List<Match> search(FileImage fileImage, Query query) {

        final Map<Word, Set<Long>> queriedIndexes = new HashMap<>();
        final Map<Word, Set<Long>> indexedWords = fileImage.getWordIndexes();

        for (Word word : query.getWords()) {
            queriedIndexes.put(word, indexedWords.getOrDefault(word, Collections.emptySet()));
        }

        final List<Match> matchData = new ArrayList<>();
        for (int i = 0; i < query.getWords().size(); i++) {
            final List<Match> match = match(i, queriedIndexes, query);
            matchData.addAll(match);
        }

        return Collections.unmodifiableList(matchData);
    }

    private List<Match> match(int startIndex, Map<Word, Set<Long>> queriedIndexes, Query query) {
        final List<Set<Long>> matchingWords = new ArrayList<>();
        for (Word word : query.getWords().subList(startIndex, query.getWords().size())) {
            matchingWords.add(new HashSet<>(queriedIndexes.get(word)));
        }

        return identifySequences(matchingWords);
    }

    private List<Match> identifySequences(List<Set<Long>> matchingWords) {
        final List<Set<Long>> matchPrefixes = new ArrayList<>();
        for (Set<Long> indexes : matchingWords) {
            if (indexes.isEmpty()) {
                break;
            }

            matchPrefixes.add(indexes);
        }

        if (matchPrefixes.isEmpty()) {
            return Collections.emptyList();
        }

        final Stack<List<Long>> possibleMatches = new Stack<>();
        for (long index : matchPrefixes.get(0)) {
            possibleMatches.add(Collections.singletonList(index));
        }

        final List<Match> matches = new ArrayList<>();
        while (!possibleMatches.isEmpty()) {
            final List<Long> currentMatch = possibleMatches.pop();

            if (currentMatch.size() == matchPrefixes.size()) {
                matches.add(new Match(Collections.unmodifiableList(currentMatch)));
                break;
            }

            final Set<Long> possibleNextPositions = new HashSet<>(matchPrefixes.get(currentMatch.size()));
            final Set<Long> allNextPositions = getSuccessors(Collections.singleton(currentMatch.get(currentMatch.size() - 1)));
            possibleNextPositions.retainAll(allNextPositions);

            for (long nextPosition : possibleNextPositions) {
                final List<Long> newMatch = new ArrayList<>(currentMatch);
                newMatch.add(nextPosition);
                possibleMatches.add(newMatch);
            }
        }

        return matches;
    }

    private Set<Long> getSuccessors(Set<Long> currentPositions) {
        final Set<Long> successors = new HashSet<>();
        for (long current : currentPositions) {
            for (int i = 1; i <= MAX_GAP; i++) {
                successors.add(current + i);
            }
        }
        return Collections.unmodifiableSet(successors);
    }

}
