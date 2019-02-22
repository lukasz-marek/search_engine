package com.randomcorp.file.normalization;

import java.util.List;

public interface LineSplitter {
    List<String> split(String line);
}
