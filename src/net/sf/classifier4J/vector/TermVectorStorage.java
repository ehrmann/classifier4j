
package net.sf.classifier4J.vector;

import java.util.Map;


public interface TermVectorStorage<C,W> {
    public void addTermVector(C category, Map<W, Integer> termVector);
    public Map<W, Integer> getTermVector(C category);
}
