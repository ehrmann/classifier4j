package net.sf.classifier4J.util;

public interface MapEntryFlattener<K, LeftV, RightV, ResultV> {
	public ResultV flatten(K key, LeftV leftValue, RightV rightValue);
}