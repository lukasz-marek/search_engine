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

    private final byte[] compressedFile;

    private FileImage(List<List<Word>> lines, String name) {

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

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            final ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);

            objectOut.writeObject(indexes);
            objectOut.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not serialize file.");
        }
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
        final ByteArrayInputStream bais = new ByteArrayInputStream(compressedFile);
        try {
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            return Collections.unmodifiableMap((Map<Word, Set<Long>>) objectIn.readObject());
        }catch(IOException | ClassNotFoundException ex){
            throw new IllegalStateException("Could not deserialize compressed file.");
        }
    }

    public String getName() {
        return name;
    }
}
