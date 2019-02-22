package com.randomcorp.file.image;

import com.randomcorp.file.normalization.LineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import com.randomcorp.processing.vocabulary.Word;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileImage {

    private final List<List<Word>> lines;

    private final Map<Word, Set<Long>> wordIndexes;

    private FileImage(List<List<Word>> lines) {
        this.lines = lines;

        final Map<Word, Set<Long>> indexes = new HashMap<>();
        long index = 0;
        for(List<Word> line : lines){
            for(Word word : line){
                if(!indexes.containsKey(word)){
                    indexes.put(word, new HashSet<>());
                }
                indexes.get(word).add(index);
                index++;
            }
        }
        indexes.replaceAll((k, v) -> Collections.unmodifiableSet(v));
        this.wordIndexes = Collections.unmodifiableMap(indexes);
    }


    public static FileImage of(File textFile, VocabularyRegistryImpl registry, LineSplitter lineSplitter) throws IOException {
        final List<List<Word>> lines = new ArrayList<>();

        String line = null;
        final BufferedReader reader = new BufferedReader(new FileReader(textFile));
        while ((line = reader.readLine()) != null) {
            final List<String> seperateWords = lineSplitter.split(line);
            final List<Word> registeredWords = seperateWords.stream()
                    .map(registry::registerAsWord).collect(Collectors.toList());

            lines.add(Collections.unmodifiableList(registeredWords));
        }

        return new FileImage(lines);
    }


    public List<List<Word>> getLines() {
        return lines;
    }

    public Map<Word, Set<Long>> getWordIndexes() {
        return wordIndexes;
    }
}
