package com.randomcorp.processing.vocabulary;

import org.junit.Assert;
import org.junit.Test;

public class WordTest {

    @Test
    public void shouldHaveEqualHashCodesButNotBeEqual(){
        // given
        final long id1 = 1;
        final String value1 = "Yoda";

        final long id2 = 1;
        final String value2 = "Vader";
        // when
        final Word word1 = new Word(id1, value1);
        final Word word2 = new Word(id2, value2);

        // then
        Assert.assertEquals(word1.hashCode(), word2.hashCode());
        Assert.assertNotEquals(word1, word2);
    }

    @Test
    public void shouldBeEqual(){
        // given
        final long id1 = 1;
        final String value1 = "Yoda";

        // when
        final Word word1 = new Word(id1, value1);
        final Word word2 = new Word(id1, value1);

        // then
        Assert.assertEquals(word1.hashCode(), word2.hashCode());
        Assert.assertEquals(word1, word2);
    }
}
