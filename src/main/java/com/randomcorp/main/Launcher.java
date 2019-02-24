package com.randomcorp.main;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.file.normalization.LineSplitter;
import com.randomcorp.file.normalization.WhitespaceLineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.VocabularyRegistryImpl;
import com.randomcorp.processing.vocabulary.Word;
import com.randomcorp.search.matching.Match;
import com.randomcorp.search.matching.Matcher;
import com.randomcorp.search.matching.Query;
import com.randomcorp.search.matching.SequenceIdentifyingMatcher;
import com.randomcorp.search.ranking.DefaultRankingStrategy;
import com.randomcorp.search.ranking.RankingResult;
import com.randomcorp.search.ranking.RankingStrategy;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Launcher {

    private final static String EXIT_COMMAND = ":quit";

    private final static String PROMPT = "search> ";

    private final static String TEXT_FILE_SUFFIX = ".txt";

    private final static long RESULT_LIMIT = 10;

    private final static LineSplitter LINE_SPLITTER = new WhitespaceLineSplitter();

    public static void main(String[] args) {

        final File searchDirectory = checkPreconditionsAndGetDirectory(args);
        final File[] contents = searchDirectory.listFiles();

        System.out.println("Loading files, please wait");

        final VocabularyRegistry registry = new VocabularyRegistryImpl(String::trim);
        final List<FileImage> fileImages = Arrays.stream(contents)
                .parallel()
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(TEXT_FILE_SUFFIX))
                .filter(file -> !file.isHidden())
                .map(file -> convert(file, registry))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        System.gc();
        System.out.format("%d files loaded\n", fileImages.size());

        final Matcher searchEngine = new SequenceIdentifyingMatcher();
        final RankingStrategy rankingStrategy = new DefaultRankingStrategy();

        final Scanner inputSource = new Scanner(System.in);

        String input = null;
        do {
            System.out.print(PROMPT);
            input = inputSource.nextLine().trim();

            if (input.length() == 0) {
                continue;
            }

            if (input.equals(EXIT_COMMAND)) {
                break;
            }

            performSearch(input, registry, searchEngine, rankingStrategy, fileImages);
        } while (true);

        inputSource.close();
    }

    private static void performSearch(String input, VocabularyRegistry registry, Matcher searchEngine,
                                      RankingStrategy rankingStrategy, List<FileImage> fileImages) {

        final List<Word> queryContents = LINE_SPLITTER.split(input).stream()
                .map(registry::getRegisteredWord)
                .collect(Collectors.toList());

        final Query query = new Query(Collections.unmodifiableList(queryContents));
        final Map<String, Integer> rankingResults = new ConcurrentHashMap<>();

        fileImages.parallelStream().forEach(fileImage -> {
            final List<Match> searchResult = searchEngine.search(fileImage, query);
            final RankingResult rankingResult = rankingStrategy.rank(searchResult, query);
            rankingResults.put(fileImage.getName(), rankingResult.getValue());
        });

        final List<String> bestMatches = rankingResults.keySet()
                .stream().sorted((str1, str2) -> Integer.compare(rankingResults.get(str2), rankingResults.get(str1)))
                .limit(RESULT_LIMIT)
                .collect(Collectors.toList());
        for (String filename : bestMatches) {
            System.out.format("%s: %d%%\n", filename, rankingResults.get(filename));
        }
    }

    private static File checkPreconditionsAndGetDirectory(String[] args) {
        if (args.length != 1) {
            System.err.println("A single argument (path to search directory) is expected.");
            System.exit(-1);
        }

        final File searchDirectory = new File(args[0]);

        if (!searchDirectory.exists()) {
            System.err.format("Path %s does not exist\n", args[0]);
            System.exit(-2);
        }

        if (!searchDirectory.isDirectory()) {
            System.err.format("%s is not a directory\n", args[0]);
            System.exit(-3);
        }

        final File[] contents = searchDirectory.listFiles();
        if (contents == null) {
            System.err.format("%s is empty\n", args[0]);
            System.exit(-4);
        }

        return searchDirectory;
    }

    private static FileImage convert(File file, VocabularyRegistry vocabularyRegistry) {
        try {
            return FileImage.of(file, vocabularyRegistry, LINE_SPLITTER);
        } catch (IOException e) {
            System.err.format("Could not read file %s, ignoring.\n", file.getName());
            return null;
        }
    }
}
