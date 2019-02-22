package com.randomcorp.processing.vocabulary;

interface VocabularyRegistry {

    Word UNKNOWN_WORD = new Word(-1L, "");

    Word registerAsWord(String word);

    /**
     * @return Word with id >=0 if the word is known, UNKNOWN_WORD with id==-1 otherwise.
     */
    Word getRegisteredWord(String word);
}
