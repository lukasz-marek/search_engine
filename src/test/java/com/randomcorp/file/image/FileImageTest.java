package com.randomcorp.file.image;

import com.randomcorp.file.normalization.WhitespaceLineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileImageTest {

    private final static String FILE_NAME = "testfile.txt";

    @Test
    public void shouldContainWordsInCorrectPlaces() throws IOException {
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(word -> word);
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        Assert.assertTrue(fileImage.getWordIndexes().get(registry.getRegisteredWord("General")).contains(0l));
        Assert.assertTrue(fileImage.getWordIndexes().get(registry.getRegisteredWord("Kenobi.")).contains(1l));
    }

    @Test
    public void shouldBeCaseSensitive() throws IOException {
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(word -> word);
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        Assert.assertFalse(fileImage.getWordIndexes().containsKey(registry.getRegisteredWord("general")));
        Assert.assertFalse(fileImage.getWordIndexes().containsKey(registry.getRegisteredWord("kenobi.")));
    }

    @Test
    public void shouldContainCorrectNumberOfPositions() throws IOException {
        final ClassLoader classLoader = FileImageTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(FILE_NAME).getFile());
        final VocabularyRegistry registry = new VocabularyRegistryImpl(word -> word);
        final FileImage fileImage = FileImage.of(file, registry, new WhitespaceLineSplitter());

        Assert.assertEquals(fileImage.getWordIndexes().get(registry.getRegisteredWord("Kenobi.")).size(), 2);
    }
}
