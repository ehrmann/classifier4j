package com.davidehrmann.classifier4j;

import java.util.Collections;
import java.util.Set;

public class SetStopWordProvider<W> implements StopWordProvider<W> {

	protected final Set<W> stopWords;
	
	public SetStopWordProvider(Set<W> stopWords) {
		this.stopWords = Collections.unmodifiableSet(stopWords);
	}
	
	@Override
	public boolean isStopWord(W word) {
		return stopWords.contains(word);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": <" + stopWords.size() + " words>";
	}
}
