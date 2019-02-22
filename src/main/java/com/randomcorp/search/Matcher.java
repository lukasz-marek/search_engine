package com.randomcorp.search;

import com.randomcorp.file.image.FileImage;

/**
 * Interface describing Matcher classes.
 */
public interface Matcher {

    /**
     *
     * @param fileImage File to scan.
     * @param query Sequence of words to be found.
     * @return Length of the longest match.
     */
    SearchResult search(FileImage fileImage, Query query);
}
