package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;

import java.util.*;
import java.util.stream.Collectors;


public class SequenceIdentifyingMatcher implements Matcher {

    private final int MAX_GAP = 3;

    @Override
    public List<Match> search(FileImage fileImage, Query query) {

        final Map<Word, Set<Long>> queriedWordsOccurrences = getQueriedWordsOccurrences(fileImage, query);

        final List<Match> bestMatchesSoFar = new ArrayList<>();
        int maxMatchLength = 0;
        for (int startIndex = 0; startIndex < query.getWords().size(); startIndex++) {
            final List<Match> matches = match(startIndex, maxMatchLength, queriedWordsOccurrences, query);

            maxMatchLength = Math.max(getBestMatchLength(matches), maxMatchLength);
            final int bestMatchLength = maxMatchLength;
            final List<Match> goodMatches = matches.stream()
                    .filter(match -> match.getOccurrences().size() == bestMatchLength)
                    .collect(Collectors.toList());

            bestMatchesSoFar.addAll(goodMatches);
            if (maxMatchLength >= query.getWords().size() - startIndex) {
                // there's no way to obtain a better result, so the search may now terminate
                break;
            }
        }

        final int bestMatchLength = maxMatchLength;
        final List<Match> bestMatches = bestMatchesSoFar.stream()
                .filter(match -> match.getOccurrences().size() == bestMatchLength)
                .collect(Collectors.toList());

        return Collections.unmodifiableList(bestMatches);
    }

    private Map<Word, Set<Long>> getQueriedWordsOccurrences(FileImage fileImage, Query query) {
        final Map<Word, Set<Long>> allWordsOccurrences = fileImage.getWordOccurrences();
        final Map<Word, Set<Long>> queriedWordsOccurrences = new HashMap<>();

        for (Word word : query.getWords()) {
            queriedWordsOccurrences.put(word, allWordsOccurrences.getOrDefault(word, Collections.emptySet()));
        }

        return Collections.unmodifiableMap(queriedWordsOccurrences);
    }

    private int getBestMatchLength(List<Match> matches) {
        return matches.isEmpty() ? 0 : matches.stream()
                .map(match -> match.getOccurrences().size())
                .max(Comparator.naturalOrder())
                .get();
    }

    private List<Match> match(int startIndex, int currentBestMatchLength, Map<Word, Set<Long>> wordOccurrences, Query query) {
        final List<Set<Long>> orderedOccurrences = new ArrayList<>();
        for (Word word : query.getWords().subList(startIndex, query.getWords().size())) {
            final Set<Long> occurrences = wordOccurrences.get(word);
            if (occurrences.isEmpty()) {
                break;
            }
            orderedOccurrences.add(occurrences);
        }

        return orderedOccurrences.isEmpty() ? Collections.emptyList() : identifySequences(orderedOccurrences, currentBestMatchLength);
    }

    private List<Match> identifySequences(List<Set<Long>> matchingWords, int currentBestMatchLength) {

        final Stack<List<Long>> possibleMatches = new Stack<>();
        for (long index : matchingWords.get(0)) {
            possibleMatches.add(Collections.singletonList(index));
        }

        final List<Match> matches = new ArrayList<>();
        while (!possibleMatches.isEmpty()) {
            final List<Long> currentMatch = possibleMatches.pop();

            if (currentMatch.size() == matchingWords.size()) {
                currentBestMatchLength = Math.max(currentBestMatchLength, currentMatch.size());
                matches.add(new Match(currentMatch));
                continue;
            }

            final Set<Long> possibleNextPositions = matchingWords.get(currentMatch.size());
            final long lastPosition = currentMatch.get(currentMatch.size() - 1);

            final Set<Long> successors= getSuccessors(lastPosition, possibleNextPositions);

            if (successors.isEmpty()) {
                currentBestMatchLength = Math.max(currentBestMatchLength, currentMatch.size());
                if (currentMatch.size() >= currentBestMatchLength) {
                    matches.add(new Match(currentMatch));
                }
                continue;
            }

            for(long successor : successors) {
                final List<Long> newMatch = new ArrayList<>(currentMatch);
                newMatch.add(successor);
                possibleMatches.push(newMatch);
            }
        }

        return matches;
    }

    private Set<Long> getSuccessors(long lastPosition, Set<Long> possibleNextPositions) {

        final Set<Long> successors = new HashSet<>();

        for (int i = 1; i <= MAX_GAP; i++) {

            final long successor = lastPosition + i;
            if (possibleNextPositions.contains(successor)) {
                successors.add(successor);
            }
        }
        return successors;
    }

}
