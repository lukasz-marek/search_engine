package com.randomcorp.main;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.file.normalization.WhitespaceLineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import com.randomcorp.processing.vocabulary.WordNormalizer;
import com.randomcorp.search.matching.SearchResult;
import com.randomcorp.search.matching.SequenceIdentifyingMatcher;
import com.randomcorp.search.matching.Matcher;
import com.randomcorp.search.matching.Query;
import com.randomcorp.search.ranking.DefaultRankingStrategy;
import com.randomcorp.search.ranking.RankingResult;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Launcher {
    public static void main(String[] args) {
        System.out.println(args[0]);

        final File directory = new File(args[0]);
        final File[] listOfFiles = directory.listFiles();
        final VocabularyRegistry registry = new VocabularyRegistryImpl(new WordNormalizer() {
            @Override
            public String normalize(String word) {
                return word.trim();
            }
        });

        FileImage img = null;
        for(File f : listOfFiles){
            if(f.isFile() && !f.isHidden()) {
                try {
                    img = FileImage.of(f, registry, new WhitespaceLineSplitter());//.getWordIndexes();
                    final Query query = new Query(Arrays.asList(registry.getRegisteredWord("stronę")));
                    Matcher m = new SequenceIdentifyingMatcher();
                    final SearchResult searchResult = m.search(img, query);
                    final RankingResult rankingResult = new DefaultRankingStrategy().rank(searchResult, query);
                    System.out.println(String.format("%s: %d", f.getName(), rankingResult.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
