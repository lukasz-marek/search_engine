package com.randomcorp.processing.vocabulary;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class VocabularyRegistryImpl implements VocabularyRegistry {

    private final AtomicLong idGenerator = new AtomicLong(0);

    private final ConcurrentHashMap<String, Word> idRegistry = new ConcurrentHashMap<>();

    public Word registerAsWord(String word){
        if (idRegistry.containsKey(word)){
            return idRegistry.get(word);
        }

        return registerIfAbsent(word);
    }


    public Optional<Word> getRegisteredWord(String word){
        return Optional.empty();
    }


    private synchronized Word registerIfAbsent(String word){
        if(!idRegistry.containsKey(word)){
            final Word newWord = new Word(idGenerator.getAndIncrement(), word);
            idRegistry.put(word, newWord);
            return newWord;
        }

        return idRegistry.get(word);
    }
}
