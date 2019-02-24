package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;

import java.util.*;
import java.util.stream.Collectors;


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
        int maxMatchLength = 0;
        for (int i = 0; i < query.getWords().size(); i++) {
            final List<Match> matches = match(i, queriedIndexes, query);

            if (!matches.isEmpty()) {
                final int bestMatchLength = matches.stream()
                        .map(match -> match.getPlaces().size())
                        .max(Comparator.naturalOrder())
                        .get();
                maxMatchLength = Math.max(bestMatchLength, maxMatchLength);
            }

            matchData.addAll(matches);
            if (maxMatchLength >= query.getWords().size() - i) {
                // there's no way to obtain a better result, so the search may now terminate
                break;
            }
        }

        final int bestMatchLength = maxMatchLength;
        final List<Match> bestMatches = matchData.stream()
                .filter(match -> match.getPlaces().size() == bestMatchLength)
                .collect(Collectors.toList());

        return Collections.unmodifiableList(bestMatches);
    }

    private List<Match> match(int startIndex, Map<Word, Set<Long>> queriedIndexes, Query query) {
        final List<Set<Long>> matchingWords = new ArrayList<>();
        for (Word word : query.getWords().subList(startIndex, queriedIndexes.size())) {
            matchingWords.add(queriedIndexes.get(word));
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
                continue;
            }

            final Set<Long> possibleNextPositions = matchPrefixes.get(currentMatch.size());
            final long lastPosition = currentMatch.get(currentMatch.size() - 1);

            long successor = -1;
            for (int i = 1; i <= MAX_GAP; i++) {
                successor = lastPosition + i;
                if (possibleNextPositions.contains(successor)) {
                    break;
                } else {
                    successor = -1;
                }
            }

            if (successor < 0) {
                matches.add(new Match(Collections.unmodifiableList(currentMatch)));
                continue;
            }

            final List<Long> newMatch = new ArrayList<>(currentMatch);
            newMatch.add(successor);
            possibleMatches.add(newMatch);
        }

        return matches;
    }

}
