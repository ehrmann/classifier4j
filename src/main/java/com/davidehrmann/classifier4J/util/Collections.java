package com.davidehrmann.classifier4j.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Collections {

	public static <K> Map<K, Integer> frequencyMap(Collection<K> items) {
    	Map<K, Integer> frequencyMap = new HashMap<K, Integer>();

    	for (K item : items) {
    		if (frequencyMap.containsKey(item)) {
    			frequencyMap.put(item, frequencyMap.get(item) + 1);
    		} else {
    			frequencyMap.put(item, 1);
    		}
    	}
    	
    	return frequencyMap;
	}
	
}
