package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.file.image.FileImageTest;
import com.randomcorp.file.normalization.WhitespaceLineSplitter;
import com.randomcorp.processing.vocabulary.IdentityWordNormalizer;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SequenceIdentifyingMatcherTest {

    private SequenceIdentifyingMatcher matcher = new SequenceIdentifyingMatcher();

    private final static String FILE_NAME = "testfile.txt";

    @Test
    public void shouldFindFullMatch() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "You must see this droid safely delivered to him on Alderaan.";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(query.getWords().size(),
                result.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get().longValue());

    }

    @Test
    public void shouldMatchSingleWord() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "Kenobi.";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(query.getWords().size(),
                result.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get().longValue());
    }

    @Test
    public void shouldNotMatchAnyWords() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "Sith";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void shouldMatchWithGap() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "droid delivered"; // in test file: "droid safely delivered"
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(query.getWords().size(),
                result.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get().longValue());
    }

    @Test
    public void shouldMatchWithSuffix() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "droid delivered message"; // in test file: "droid safely delivered"
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2,
                result.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get().longValue());
    }

    @Test
    public void shouldMatchWithPrefix() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "wayward droid delivered"; // in test file: "droid safely delivered"
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2,
                result.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get().longValue());
    }

    @Test
    public void shouldIgnoreNewLine() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "it. You must"; // in testfile: "it.\n You must"
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));

        //when
        final List<Match> result = matcher.search(fileImage, query);

        //then
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(3,
                result.stream().map(Match::getOccurrences).map(List::size).max(Comparator.naturalOrder()).get().longValue());
    }
}
