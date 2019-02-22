package com.randomcorp.processing.vocabulary;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe implementation of com.randomcorp.processing.vocabulary.VocabularyRegistry.
 */
public final class VocabularyRegistryImpl implements VocabularyRegistry {

    private final AtomicLong idGenerator = new AtomicLong(0);

    private final ConcurrentHashMap<String, Word> idRegistry = new ConcurrentHashMap<>();

    private final WordNormalizer wordNormalizer;

    public VocabularyRegistryImpl(WordNormalizer wordNormalizer){
        this.wordNormalizer = wordNormalizer;
    }

    public Word registerAsWord(String word){
        word = wordNormalizer.normalize(word);

        if (idRegistry.containsKey(word)){
            return idRegistry.get(word);
        }

        return registerIfAbsent(word);
    }


    public Word getRegisteredWord(String word){
        word = wordNormalizer.normalize(word);

        return idRegistry.getOrDefault(word, VocabularyRegistry.UNKNOWN_WORD);
    }


    private Word registerIfAbsent(String word){
        /*
         * This method could be replaced by a simple idRegistry.putIfAbsent,
         * but this would mean that some values of idGenerator would be unused.
         * Therefore, a minimal amount of synchronization is required
         */
        synchronized (idRegistry) {
            if (!idRegistry.containsKey(word)) {
                final Word newWord = new Word(idGenerator.getAndIncrement(), word);
                idRegistry.put(word, newWord);
                return newWord;
            }
        }

        return idRegistry.get(word);
    }
}
