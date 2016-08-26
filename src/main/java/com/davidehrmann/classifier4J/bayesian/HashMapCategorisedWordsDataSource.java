package com.davidehrmann.classifier4j.bayesian;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashMapCategorisedWordsDataSource<W,C> implements ICategorisedWordsDataSource<W,C> {

	protected final Map<WordProbability<W,C>, WordProbability<W,C>> wordProbabilities = new HashMap<WordProbability<W,C>, WordProbability<W,C>>();
	
	public HashMapCategorisedWordsDataSource() {
		
	}
	
	public HashMapCategorisedWordsDataSource(Set<WordProbability<W,C>> backingSet) {
		for (WordProbability<W,C> wp : backingSet) {
			wordProbabilities.put(wp, wp);
		}
	}
	
	@Override
	public WordProbability<W, C> getWordProbability(W word)
			throws WordsDataSourceException {
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
	public WordProbability<W, C> getWordProbability(C category, W word) throws WordsDataSourceException {
		return wordProbabilities.get(new  WordProbability<W, C>(category, word));
	}

	@Override
	public void addMatch(C category, W word) throws WordsDataSourceException {
		WordProbability<W, C> key = new WordProbability<W, C>(category, word);
		WordProbability<W,C> value = wordProbabilities.get(key);
		
		if (value == null) {
			value = key;
			wordProbabilities.put(key, value);
		}
		
		value.registerMatch();
	}

	@Override
	public void addNonMatch(C category, W word) throws WordsDataSourceException {
		WordProbability<W, C> key = new WordProbability<W, C>(category, word);
		WordProbability<W,C> value = wordProbabilities.get(key);
		
		if (value == null) {
			value = key;
			wordProbabilities.put(key, value);
		}
		
		value.registerNonMatch();
	}

	public Set<WordProbability<W, C>> getBackingSet() {
		return Collections.unmodifiableSet(wordProbabilities.keySet());
	}
}
