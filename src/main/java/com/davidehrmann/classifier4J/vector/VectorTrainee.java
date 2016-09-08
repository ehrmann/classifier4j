
package com.davidehrmann.classifier4j.vector;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.davidehrmann.classifier4j.ClassifierException;
import com.davidehrmann.classifier4j.tokenizer.Tokenizer;
import com.davidehrmann.classifier4j.tokenizer.Trainable;
import com.davidehrmann.classifier4j.StopWordProvider;
import com.davidehrmann.classifier4j.Utilities;


public class VectorTrainee<C,I> implements Trainable<C,I> {

    private final int numTermsInVector = 50;
    
    private final Tokenizer<I> tokenizer;
    private final StopWordProvider<I> stopWordsProvider;
    private final TermVectorStorage<C,I> storage;    
        
    public VectorTrainee(TermVectorStorage<C,I> storage, Tokenizer<I> tokenizer, StopWordProvider<I> stopWordsProvider) {
        this.tokenizer = tokenizer;
        this.stopWordsProvider = stopWordsProvider;
        this.storage = storage;
        
        setMatchCutoff(DEFAULT_VECTORCLASSIFIER_CUTOFF);
    }   

    /**
     * @see Trainable#teachMatch(java.lang.String, java.lang.String)
     */
    public void teachMatch(C category, I input) throws ClassifierException {
        // Create a map of the word frequency from the input
        Map<I,Integer> wordFrequencies = Utilities.getWordFrequency(input, tokenizer, stopWordsProvider);
        
        // get the numTermsInVector most used words in the input
        Set<I> mostFrequentWords = Utilities.getMostFrequentWords(numTermsInVector, wordFrequencies);

        List<I> terms = new ArrayList<I>(mostFrequentWords);
        List<Integer> values = generateTermValuesVector(terms, wordFrequencies);
        TermVector<I> tv = new TermVector<I>(terms, values);
        
        storage.addTermVector(category, tv);        
        
        return;
    }
    
    /**
     * @see Trainable#teachNonMatch(java.lang.String, java.lang.String)
     */
    public void teachNonMatch(C category, I input) throws ClassifierException {
        return; // this is not required for the VectorClassifier        
    }
    
    public static class NMostFrequentMap<I> extends AbstractMap<I, Integer> {
		@Override
		public Set<java.util.Map.Entry<I, Integer>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }
}
