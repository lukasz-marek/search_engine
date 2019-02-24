package com.randomcorp.search.ranking;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.file.image.FileImageTest;
import com.randomcorp.file.normalization.WhitespaceLineSplitter;
import com.randomcorp.processing.vocabulary.IdentityWordNormalizer;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import com.randomcorp.search.matching.Match;
import com.randomcorp.search.matching.Query;
import com.randomcorp.search.matching.SequenceIdentifyingMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRankingStrategyTest {

    private SequenceIdentifyingMatcher matcher = new SequenceIdentifyingMatcher();

    private DefaultRankingStrategy rankingStrategy = new DefaultRankingStrategy();

    private final static String FILE_NAME = "testfile.txt";

    @Test
    public void shouldReturn100() throws IOException {
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
        final List<Match> result = matcher.search(fileImage, query);

        //when
        final RankingResult rankingResult = rankingStrategy.rank(result, query);

        // then
        Assert.assertEquals(100, rankingResult.getValue());
    }

    @Test
    public void shouldReturn0() throws IOException {
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
        final List<Match> result = matcher.search(fileImage, query);

        //when
        final RankingResult rankingResult = rankingStrategy.rank(result, query);

        //then
        Assert.assertEquals(0, rankingResult.getValue());
    }

    @Test
    public void shouldReturn33() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "General Obi Kenobi.";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));
        final List<Match> result = matcher.search(fileImage, query);

        //when
        final RankingResult rankingResult = rankingStrategy.rank(result, query);

        //then
        /* Internally, there are some roundings, so it's better to check
        if the value if "close enough"
        */
        Assert.assertTrue(rankingResult.getValue() >= 32);
        Assert.assertTrue(rankingResult.getValue() <= 34);
    }


    @Test
    public void shouldReturn100ForTwoWords() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "droid delivered";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));
        final List<Match> result = matcher.search(fileImage, query);

        //when
        final RankingResult rankingResult = rankingStrategy.rank(result, query);

        //then
        Assert.assertEquals(100, rankingResult.getValue());
    }

    @Test
    public void shouldReturn66WithUnknownPrefix() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "fat droid delivered";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));
        final List<Match> result = matcher.search(fileImage, query);

        //when
        final RankingResult rankingResult = rankingStrategy.rank(result, query);

        //then
        /* Internally, there are some roundings, so it's better to check
        if the value if "close enough"
        */
        Assert.assertTrue(rankingResult.getValue() >= 65);
        Assert.assertTrue(rankingResult.getValue() <= 67);
    }

    @Test
    public void shouldReturn66WithUnknownSuffix() throws IOException {
        //given
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new IdentityWordNormalizer());
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        final String queryString = "droid delivered message";
        final Query query = new Query(new WhitespaceLineSplitter().split(queryString)
                .stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList()));
        final List<Match> result = matcher.search(fileImage, query);

        //when
        final RankingResult rankingResult = rankingStrategy.rank(result, query);

        //then
        /* Internally, there are some roundings, so it's better to check
        if the value if "close enough"
        */
        Assert.assertTrue(rankingResult.getValue() >= 65);
        Assert.assertTrue(rankingResult.getValue() <= 67);
    }
}
