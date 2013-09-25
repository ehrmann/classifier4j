/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to [http://unlicense.org]
 */

package net.sf.classifier4J.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LeftJoinMap<K, LeftV, RightV, ResultV> extends AbstractMap<K, ResultV> {

	protected final Reducer<K, LeftV, RightV, ResultV> reducer;
	protected final Map<K, LeftV> left;
	protected final Map<K, RightV> right;
	
	public LeftJoinMap(Map<K, LeftV> left, Map<K, RightV> right, Reducer<K, LeftV, RightV, ResultV> reducer) {
		this.left = left;
		this.right = right;
		this.reducer = reducer;
	}
	
	@Override
	public Set<java.util.Map.Entry<K, ResultV>> entrySet() {
		return new LeftJoinEntrySet();
	}

	@Override
	public int size() {
		return this.left.size();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.left.containsKey(key);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultV get(Object key) {
		LeftV leftValue = this.left.get(key);
		RightV rightValue = this.right.get(key);
		
		if (leftValue == null && !this.left.containsKey(key)) {
			return null;
		}
		
		return this.reducer.reduce((K)key, leftValue, rightValue);
	}
	
	@Override
	public Set<K> keySet() {
		return this.left.keySet();
	}

	@Override
	public Collection<ResultV> values() {
		return new LeftJoinValuesCollection();
	}

	protected class LeftJoinEntrySet extends AbstractSet<Map.Entry<K, ResultV>> {

		@Override
		public Iterator<Map.Entry<K, ResultV>> iterator() {
			return new Iterator<Map.Entry<K, ResultV>>() {
				private final Iterator<Map.Entry<K, LeftV>> leftIterator = LeftJoinMap.this.left.entrySet().iterator();
				
				@Override
				public boolean hasNext() {
					return leftIterator.hasNext();
				}

				@Override
				public Map.Entry<K, ResultV> next() {
					Map.Entry<K, LeftV> leftEntry = this.leftIterator.next();
					K key = leftEntry.getKey();
					return new AbstractMap.SimpleImmutableEntry<K, ResultV>(key, LeftJoinMap.this.reducer.reduce(key, leftEntry.getValue(), LeftJoinMap.this.right.get(key)));
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			return LeftJoinMap.this.left.size();
		}
		
	}
	
	protected class LeftJoinValuesCollection extends AbstractCollection<ResultV> {
		@Override
		public Iterator<ResultV> iterator() {
			return new Iterator<ResultV>() {
				private final Iterator<Map.Entry<K, LeftV>> leftIterator = LeftJoinMap.this.left.entrySet().iterator();
				
				@Override
				public boolean hasNext() {
					return leftIterator.hasNext();
				}

				@Override
				public ResultV next() {
					Map.Entry<K, LeftV> leftEntry = this.leftIterator.next();
					K key = leftEntry.getKey();
					return LeftJoinMap.this.reducer.reduce(key, leftEntry.getValue(), LeftJoinMap.this.right.get(key));
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			return LeftJoinMap.this.left.size();
		}
	}
	
	public interface Reducer<K, LeftV, RightV, ResultV> {
		public ResultV reduce(K key, LeftV leftValue, RightV rightValue);
	}

}
