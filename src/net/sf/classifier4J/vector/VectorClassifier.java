
package net.sf.classifier4J.vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.classifier4J.AbstractClassifier;
import net.sf.classifier4J.ClassifierException;
import net.sf.classifier4J.ICategorisedClassifier;
import net.sf.classifier4J.util.LeftJoinMap;
import net.sf.classifier4J.util.LeftJoinMap.Reducer;
import net.sf.classifier4J.vector.VectorUtils.IntegerTuple;


public class VectorClassifier<C,T> extends AbstractClassifier implements ICategorisedClassifier<C,T> {
    public static double DEFAULT_VECTORCLASSIFIER_CUTOFF = 0.80d;
    
    private final int numTermsInVector = 50;
    
    private final TermVectorStorage<C,T> storage;    
        
    public VectorClassifier(TermVectorStorage<C,T> storage) {
        this.storage = storage;
        
        setMatchCutoff(DEFAULT_VECTORCLASSIFIER_CUTOFF);
    }
    
    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#classify(java.lang.String, java.lang.String)
     */

    public double classify(C category, Collection<T> tokens) throws ClassifierException {

    	// Create a map of the word frequency from the tokens
    	// TODO: replace with map-reduce
    	Map<T, Integer> tokenFrequencies = new HashMap<T, Integer>();

    	for (T token : tokens) {
    		if (tokenFrequencies.containsKey(token)) {
    			tokenFrequencies.put(token, tokenFrequencies.get(token) + 1);
    		} else {
    			tokenFrequencies.put(token, 1);
    		}
    	}

    	// TODO: truncate tokenFrequencies with numTermsInVector
    	
    	Map<T, Integer> tv = storage.getTermVector(category);

    	Map<T, IntegerTuple> compliated = new LeftJoinMap<T, Integer, Integer, VectorUtils.IntegerTuple>(tv, tokenFrequencies, new Reducer<T, Integer, Integer, IntegerTuple>() {
    		@Override public IntegerTuple reduce(T key, Integer leftValue, Integer rightValue) {
    			return new IntegerTuple(leftValue, rightValue != null ? rightValue : 0);
    		}
    	});

    	return VectorUtils.cosineOfVectors(compliated.values());     
    }

    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#isMatch(java.lang.String, java.lang.String)
     */
    public boolean isMatch(C category, Collection<T> inputs) throws ClassifierException {
        return (getMatchCutoff() < classify(category, inputs));
    }
}
