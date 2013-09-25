
package net.sf.classifier4J.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


@Deprecated
public class TermVector<W> implements Serializable {

	private static final long serialVersionUID = -3629434516501197573L;
	
	private final List<W> terms;
    private final List<Integer> values;
    
    public TermVector(W[] terms, int[] values) {
    	if (terms.length != values.length) {
    		throw new IllegalArgumentException("terms and values aren't the same length");
    	}
    	
    	this.terms = Arrays.asList(terms);
    	this.values = new ArrayList<Integer>(values.length);
    	
    	for (int value : values) {
    		this.values.add(value);
    	}
    }
    
    public TermVector(List<W> terms, List<Integer> values) {
    	if (terms.size() != values.size()) {
    		throw new IllegalArgumentException("terms and values aren't the same length");
    	}
    	
        this.terms = terms;
        this.values = values;
    }
    
    public List<W> getTerms() {
        return Collections.unmodifiableList(terms);
    }
    
    public List<Integer> getValues() {
        return Collections.unmodifiableList(values);
    }
    
    public String toString() {
    	StringBuilder results = new StringBuilder();
    	
    	results.append('{');
    	
    	Iterator<W> termsIterator = terms.iterator();
    	Iterator<Integer> valuesIterator = values.iterator();
    	
    	while (termsIterator.hasNext() && valuesIterator.hasNext()) {
            results.append("[");
            results.append(termsIterator.next());
            results.append(", ");
            results.append(valuesIterator.next());
            results.append("] ");
    	}
    	
        results.append("}");
        
        return results.toString();
    }
}
