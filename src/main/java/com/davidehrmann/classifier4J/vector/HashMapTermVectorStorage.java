
package com.davidehrmann.classifier4j.vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class HashMapTermVectorStorage<C,W> implements TermVectorStorage<C,W> {
    private Map<C, Map<W, Integer>> storage = new HashMap<C, Map<W, Integer>>();
    
    
    /**
     * @see TermVectorStorage#addTermVector(java.lang.String, TermVector)
     */
    public void addTermVector(C category, Map<W, Integer> termVector) {
    	
    	Map<W, Integer> termMap = storage.get(category);
    	if (termMap == null) {
    		termMap = new HashMap<W, Integer>();
    		storage.put(category, termMap);
    	}
    	
    	for (Entry<W, Integer> vector : termVector.entrySet()) {
    		Integer count = termMap.get(vector.getKey());
    		
    		if (count == null) {
    			count = 0;
    		}
    		
    		termMap.put(vector.getKey(), count + vector.getValue());
    	}
    }

    /**
     * @see TermVectorStorage#getTermVector(java.lang.String)
     */
    public Map<W, Integer> getTermVector(C category) {
    	Map<W, Integer> result = storage.get(category);
    	
    	if (result == null) {
    		result = Collections.emptyMap();
    	}
    	
    	return Collections.unmodifiableMap(result);
    }

    public Map<C, Map<W, Integer>> getBackingMap() {
    	// TODO: make internal maps unmodifiable
    	return Collections.unmodifiableMap(storage);
    }
}
