package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.file.image.FileImageTest;
import com.randomcorp.file.normalization.WhitespaceLineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class SequenceIdentyfyingMatcherTest {

    private SequenceIdentifyingMatcher matcher = new SequenceIdentifyingMatcher();

    private final static String FILE_NAME = "testfile.txt";

    @Test
    public void shouldFindFullMatch() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(word -> word);
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "You must see this droid safely delivered to him on Alderaan.";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final SearchResult result = matcher.search(fileImage, query);

        //then
        Assert.assertEquals(result.getMatches().size(), 11);
        Assert.assertEquals(result.getMatches().get(0).getMaxLength(), query.getWords().size());
        Assert.assertEquals(result.getMatches().get(0).getNumberOfSequences(), 1);
    }

    @Test
    public void shouldMatchSingleWord() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(word -> word);
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "Kenobi.";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final SearchResult result = matcher.search(fileImage, query);

        //then
        Assert.assertEquals(1, result.getMatches().size());
        Assert.assertEquals(query.getWords().size(), result.getMatches().get(0).getMaxLength());
        Assert.assertEquals(2, result.getMatches().get(0).getNumberOfSequences());
    }

    @Test
    public void shouldNotMatchAnyWords() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(word -> word);
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "Sith";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final SearchResult result = matcher.search(fileImage, query);

        //then
        Assert.assertEquals(1, result.getMatches().size());
        Assert.assertEquals(0, result.getMatches().get(0).getMaxLength());
        Assert.assertEquals(0, result.getMatches().get(0).getNumberOfSequences());
    }
}
