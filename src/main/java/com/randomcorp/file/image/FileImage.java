package com.randomcorp.file.image;

import com.randomcorp.file.normalization.LineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.Word;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class FileImage {

    private final String name;

    // private final Map<Word, Set<Long>> wordOccurrences;

    private final byte[] compressedFile;

    private FileImage(List<List<Word>> lines, String name) throws IOException {

        this.name = name;

        final Map<Word, Set<Long>> indexes = new HashMap<>();
        long index = 0;
        for (List<Word> line : lines) {
            for (Word word : line) {
                if (!indexes.containsKey(word)) {
                    indexes.put(word, new HashSet<>());
                }
                indexes.get(word).add(index);
                index++;
            }
        }
        indexes.replaceAll((k, v) -> Collections.unmodifiableSet(v));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
        objectOut.writeObject(indexes);
        objectOut.writeObject(indexes);
        objectOut.close();
        this.compressedFile = baos.toByteArray();
    }


    public static FileImage of(File textFile, VocabularyRegistry registry, LineSplitter lineSplitter) throws IOException {
        final List<List<Word>> lines = new ArrayList<>();

        String line = null;
        final BufferedReader reader = new BufferedReader(new FileReader(textFile));
        while ((line = reader.readLine()) != null) {
            final List<String> seperateWords = lineSplitter.split(line);
            final List<Word> registeredWords = seperateWords.stream()
                    .map(registry::registerAsWord).collect(Collectors.toList());

            lines.add(Collections.unmodifiableList(registeredWords));
        }

        reader.close();
        return new FileImage(lines, textFile.getName());
    }

    public Map<Word, Set<Long>> getWordOccurrences() {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedFile);
        GZIPInputStream gzipIn = null;
        try {
            gzipIn = new GZIPInputStream(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectInputStream objectIn = null;
        try {
            objectIn = new ObjectInputStream(gzipIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Word, Set<Long>> wordOccurrences = null;
        try {
            wordOccurrences = (Map<Word, Set<Long>>) objectIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            objectIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableMap(wordOccurrences);
    }

    public String getName() {
        return name;
    }
}
