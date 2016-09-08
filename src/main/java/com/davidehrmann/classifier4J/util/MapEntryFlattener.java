package com.davidehrmann.classifier4j.util;

public interface MapEntryFlattener<K, LeftV, RightV, ResultV> {
	ResultV flatten(K key, LeftV leftValue, RightV rightValue);
}