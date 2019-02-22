package com.randomcorp.file.image;

import com.randomcorp.file.normalization.LineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import com.randomcorp.processing.vocabulary.Word;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileImage {

    private final Map<Word, Set<Long>> wordIndexes;

    private FileImage(Map<Word, Set<Long>> wordIndexes){
        this.wordIndexes = wordIndexes;
    }

    public static FileImage of(File textFile, VocabularyRegistryImpl registry, LineSplitter lineSplitter) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(textFile));


        Map<Word, Set<Long>> wordIndexes = new HashMap<>();
        long wordIndex = 0;
        String line = null;
        while ((line = reader.readLine()) != null){
            final List<String> seperateWords = lineSplitter.split(line);
            final List<Word> registeredWords = seperateWords.stream()
                    .map(registry::registerAsWord).collect(Collectors.toList());

            for(Word word : registeredWords){
                if(! wordIndexes.containsKey(word)){
                    wordIndexes.put(word, new HashSet<>());
                }
                final Set<Long> indexes = wordIndexes.get(word);
                indexes.add(wordIndex);
                wordIndex++;
            }

        }

        return new FileImage(wordIndexes);
    }


}
