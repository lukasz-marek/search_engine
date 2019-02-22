package com.randomcorp.processing.vocabulary;

import org.junit.Assert;
import org.junit.Test;

public class VocabularyRegistryImplTest {


    @Test
    public void shouldReturnUnknownWord(){
        // given
        final VocabularyRegistryImpl registry = new VocabularyRegistryImpl(word -> word);

        final String unknownWord = "Yoda";
        // when
        final Word wordFromRegistry = registry.getRegisteredWord(unknownWord);
        // then
        Assert.assertEquals(wordFromRegistry, VocabularyRegistry.UNKNOWN_WORD);
    }

    @Test
    public void shouldReturnTheSameWordTwice(){
        // given
        final VocabularyRegistryImpl registry = new VocabularyRegistryImpl(word -> word);

        final String newWord = "Yoda";
        // when
        final Word word1FromRegistry = registry.registerAsWord(newWord);
        final Word word2FromRegistry = registry.registerAsWord(newWord);
        // then
        Assert.assertEquals(word1FromRegistry, word2FromRegistry);
    }

    @Test
    public void shouldReturnDistinctWords(){
        // given
        final VocabularyRegistryImpl registry = new VocabularyRegistryImpl(word -> word);

        final String newWord1 = "Yoda";
        final String newWord2 = "Dooku";

        // when
        final Word word1FromRegistry = registry.registerAsWord(newWord1);
        final Word word2FromRegistry = registry.registerAsWord(newWord2);
        // then
        Assert.assertNotEquals(word1FromRegistry, word2FromRegistry);
    }

    @Test
    public void shouldReturnStoredWord(){
        // given
        final VocabularyRegistryImpl registry = new VocabularyRegistryImpl(word -> word);

        final String newWord = "Yoda";
        // when
        final Word word1FromRegistry = registry.registerAsWord(newWord);
        final Word word2FromRegistry = registry.getRegisteredWord(newWord);
        // then
        Assert.assertEquals(word1FromRegistry, word2FromRegistry);
    }
}
