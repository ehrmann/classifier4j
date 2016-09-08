package com.davidehrmann.classifier4j.bayesian;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleCategorizedWordsDataSource<W,C> implements CategorizedWordsDataSource<W,C> {

    protected final Map<WordProbability<W,C>, WordProbability<W,C>> wordProbabilities = new HashMap<>();

    public SimpleCategorizedWordsDataSource() {

    }

    public SimpleCategorizedWordsDataSource(Set<WordProbability<W,C>> backingSet) {
        for (WordProbability<W,C> wp : backingSet) {
            wordProbabilities.put(wp, wp);
        }
    }

    @Override
    public WordProbability<W,C> getWordProbability(W word) throws WordsDataSourceException {
        return getWordProbability(null, word);
    }

    @Override
    public void addMatch(W word) throws WordsDataSourceException {
        addMatch(null, word);
    }

    @Override
    public void addNonMatch(W word) throws WordsDataSourceException {
        addNonMatch(null, word);
    }

    @Override
    public WordProbability<W,C> getWordProbability(C category, W word) throws WordsDataSourceException {
        return wordProbabilities.get(new WordProbability<>(category, word));
    }

    @Override
    public void addMatch(C category, W word) throws WordsDataSourceException {
        WordProbability<W,C> key = new WordProbability<>(category, word);
        wordProbabilities.computeIfAbsent(key, k -> k).registerMatch();
    }

    @Override
    public void addNonMatch(C category, W word) throws WordsDataSourceException {
        WordProbability<W,C> key = new WordProbability<>(category, word);
        wordProbabilities.computeIfAbsent(key, k -> k).registerNonMatch();
    }

    public Set<WordProbability<W, C>> getBackingSet() {
        return Collections.unmodifiableSet(wordProbabilities.keySet());
    }
}
