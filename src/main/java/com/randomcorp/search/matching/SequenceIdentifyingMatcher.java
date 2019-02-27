package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;
import sun.nio.ch.ThreadPool;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class SequenceIdentifyingMatcher implements Matcher {

    private final int MAX_GAP = 3;

    private final ExecutorService executor = Executors.newWorkStealingPool();

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

        return orderedOccurrences.isEmpty() ? Collections.emptyList() : identifySequencesParallel(orderedOccurrences, currentBestMatchLength);
    }

    private List<Match> identifySequencesParallel(List<Set<Long>> matchingWords, int currentBestMatchLength) {
        final List<CompletableFuture<Void>> tasks = new CopyOnWriteArrayList<>();
        final Queue<Match> matches = new ConcurrentLinkedQueue<>();

        for (long index : matchingWords.get(0)) {
            tasks.add(CompletableFuture.runAsync(() -> processItem(Collections.singletonList(index), matchingWords, currentBestMatchLength, tasks, matches)));
        }

        try {

            while (tasks.stream().filter(CompletableFuture::isDone).count() < tasks.size()) {
                Thread.sleep(500);
            }

        } catch (InterruptedException e) {
            // should never happen
            tasks.forEach(task -> task.cancel(true));
        }

        return new ArrayList<>(matches);
    }

    private void processItem(List<Long> currentMatch, List<Set<Long>> matchingWords, int currentBestMatchLength, List<CompletableFuture<Void>> tasks, Queue<Match> matches) {
        if (currentMatch.size() == matchingWords.size()) {
            matches.add(new Match(currentMatch));
            return;
        }

        final Set<Long> possibleNextPositions = matchingWords.get(currentMatch.size());
        final long lastPosition = currentMatch.get(currentMatch.size() - 1);

        final Set<Long> successors = getSuccessors(lastPosition, possibleNextPositions);

        if (successors.isEmpty()) {
            if (currentMatch.size() >= currentBestMatchLength) {
                matches.add(new Match(currentMatch));
            }
            return;
        }

        tasks.add(CompletableFuture.runAsync(() -> {
            for (long successor : successors) {
                final List<Long> newMatch = new ArrayList<>(currentMatch);
                newMatch.add(successor);
                processItem(Collections.unmodifiableList(newMatch), matchingWords, currentBestMatchLength, tasks, matches);
            }
        }, executor));
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
