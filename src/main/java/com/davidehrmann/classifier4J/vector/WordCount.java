
package com.davidehrmann.classifier4j.vector;

import java.io.Serializable;


public class WordCount<W> implements Serializable {

	private static final long serialVersionUID = 7649235679961438755L;
	
	private final W word;
    private final int count;
    
    public WordCount(W word, int count) {
    	this.word = word;
    	this.count = count;
    }
    
    public W getWord() {
        return this.word;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public String toString() {
    	return "[" + this.word + ", " + this.count + "]";
    }
}
