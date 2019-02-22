package com.randomcorp.processing.vocabulary;

import java.util.Optional;

interface VocabularyRegistry {

    Word registerAsWord(String word);

    Optional<Word> getRegisteredWord(String word);
}
