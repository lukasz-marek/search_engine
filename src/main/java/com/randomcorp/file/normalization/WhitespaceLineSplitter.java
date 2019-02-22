package com.randomcorp.file.normalization;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Splits a line based on whitespace characters.
 */
public class WhitespaceLineSplitter implements LineSplitter {

    @Override
    public List<String> split(String line) {
        return Arrays.stream(line.split("\\s")).collect(Collectors.toList());
    }
}
