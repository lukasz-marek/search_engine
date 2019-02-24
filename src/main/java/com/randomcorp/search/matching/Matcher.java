package com.randomcorp.search.matching;

import com.randomcorp.file.image.FileImage;

import java.util.List;

/**
 * Interface describing Matcher classes.
 */
public interface Matcher {
    List<List<Long>>  search(FileImage fileImage, Query query);
}
