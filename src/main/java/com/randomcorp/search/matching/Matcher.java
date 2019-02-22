package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;

/**
 * Interface describing Matcher classes.
 */
public interface Matcher {
    SearchResult search(FileImage fileImage, Query query);
}
