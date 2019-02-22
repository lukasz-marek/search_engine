package com.randomcorp.processing.vocabulary;

/**
 * Default implementation of word normalizer.
 */
public class IdentityWordNormalizer implements WordNormalizer{
    @Override
    public String normalize(String word) {
        return word;
    }
}
