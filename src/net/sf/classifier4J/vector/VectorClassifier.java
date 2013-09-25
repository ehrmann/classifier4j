
package net.sf.classifier4J.vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.classifier4J.AbstractCategorizedTrainableClassifier;
import net.sf.classifier4J.ClassifierException;
import net.sf.classifier4J.IStopWordProvider;
import net.sf.classifier4J.ITokenizer;
import net.sf.classifier4J.Utilities;
import net.sf.classifier4J.util.LeftJoinMap;
import net.sf.classifier4J.util.LeftJoinMap.Reducer;
import net.sf.classifier4J.vector.VectorUtils.IntegerTuple;


public class VectorClassifier<C,I> extends AbstractCategorizedTrainableClassifier<C,I> {
    public static double DEFAULT_VECTORCLASSIFIER_CUTOFF = 0.80d;
    
    
    private final int numTermsInVector = 50;
    
    private final ITokenizer<I> tokenizer;
    private final IStopWordProvider<I> stopWordsProvider;
    private final TermVectorStorage<C,I> storage;    
        
    public VectorClassifier(TermVectorStorage<C,I> storage, ITokenizer<I> tokenizer, IStopWordProvider<I> stopWordsProvider) {
        this.tokenizer = tokenizer;
        this.stopWordsProvider = stopWordsProvider;
        this.storage = storage;
        
        setMatchCutoff(DEFAULT_VECTORCLASSIFIER_CUTOFF);
    }
    
    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#classify(java.lang.String, java.lang.String)
     */
    public double classify(C category, I input) throws ClassifierException {
        
        // Create a map of the word frequency from the input
        Map<I,Integer> wordFrequencies = Utilities.getWordFrequency(input, tokenizer, stopWordsProvider);
        
        Map<I, Integer> tv = storage.getTermVector(category);

        Map<I, IntegerTuple> compliated = new LeftJoinMap<I, Integer, Integer, VectorUtils.IntegerTuple>(tv, wordFrequencies, new Reducer<I, Integer, Integer, IntegerTuple>() {

			@Override
			public IntegerTuple reduce(I key, Integer leftValue,
					Integer rightValue) {
				// TODO Auto-generated method stub
				return null;
			}
        	
		});
        
        	List<Integer> inputValues = generateTermValuesVector(tv.keySet(), wordFrequencies);
            return VectorUtils.cosineOfVectors(inputValues, tv.getValues());     
    }

    public double classify(C category, Collection<I> inputs) throws ClassifierException {
        
    	// Create a map of the word frequency from the input
    	Map<I,Integer> wordFrequencies = new HashMap<I,Integer>();
    	for (I input : inputs) {
    		Map<I,Integer> partialWordFrequencies = Utilities.getWordFrequency(input, tokenizer, stopWordsProvider);
    	
    		for (Entry<I,Integer> entry : partialWordFrequencies.entrySet()) {
    			if (wordFrequencies.containsKey(entry.getKey())) {
    				wordFrequencies.put(entry.getKey(), entry.getValue() + wordFrequencies.get(entry.getKey()));
    			} else {
    				wordFrequencies.put(entry.getKey(), entry.getValue());
    			}
    		}
    	}
        
        TermVector<I> tv = storage.getTermVector(category);
        if (tv == null) {
            return 0;
        } else {
        	List<Integer> inputValues = generateTermValuesVector(tv.getTerms(), wordFrequencies);
        	
        	int matched = 0;
        	for (Integer i : inputValues) {
        		if (i > 0) matched++;
        	}
        	
            return VectorUtils.cosineOfVectors(inputValues, tv.getValues());
        }        
    }

    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#isMatch(java.lang.String, java.lang.String)
     */
    public boolean isMatch(C category, I input) throws ClassifierException {
        return (getMatchCutoff() < classify(category, input));
    }

    

    /**
     * @see net.sf.classifier4J.ITrainable#teachMatch(java.lang.String, java.lang.String)
     */
    public void teachMatch(C category, I input) throws ClassifierException {
        // Create a map of the word frequency from the input
        Map<I,Integer> wordFrequencies = Utilities.getWordFrequency(input, tokenizer, stopWordsProvider);
        
        if (wordFrequencies.size() == 0) { return; }
        
        // get the numTermsInVector most used words in the input
        Set<I> mostFrequentWords = Utilities.getMostFrequentWords(numTermsInVector, wordFrequencies);

        List<I> terms = new ArrayList<I>(mostFrequentWords);
        List<Integer> values = generateTermValuesVector(terms, wordFrequencies);
        TermVector<I> tv = new TermVector<I>(terms, values);
        
        storage.addTermVector(category, tv);        
        
        return;
    }

    /**
     * @param terms
     * @param wordFrequencies
     * @return
     */
    // Replace with augmented map
    protected List<Integer> generateTermValuesVector(List<I> terms, Map<I, Integer> wordFrequencies) {
    	List<Integer> result = new ArrayList<Integer>(terms.size());
        for (I term : terms) {
            Integer value = wordFrequencies.get(term);
            result.add(value != null ? value : 0);            
        }
        return result;
    }


    /**
     * @see net.sf.classifier4J.ITrainable#teachNonMatch(java.lang.String, java.lang.String)
     */
    public void teachNonMatch(C category, I input) throws ClassifierException {
        return; // this is not required for the VectorClassifier        
    }
}
