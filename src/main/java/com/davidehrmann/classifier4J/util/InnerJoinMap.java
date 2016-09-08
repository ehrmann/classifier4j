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

package com.davidehrmann.classifier4j.util;

import java.util.*;

public class InnerJoinMap<K, LeftV, RightV, ResultV> extends AbstractMap<K, ResultV> {

	protected final MapEntryFlattener<K, LeftV, RightV, ResultV> reducer;
	protected final Map<K, LeftV> left;
	protected final Map<K, RightV> right;
	
	public InnerJoinMap(Map<K, LeftV> left, Map<K, RightV> right, MapEntryFlattener<K, LeftV, RightV, ResultV> reducer) {
		this.left = left;
		this.right = right;
		this.reducer = reducer;
	}
	
	@Override
	public Set<java.util.Map.Entry<K, ResultV>> entrySet() {
		return new InnerJoinEntrySet();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.left.containsKey(key) && this.right.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultV get(Object key) {
		LeftV leftValue = this.left.get(key);
		RightV rightValue = this.right.get(key);
		
		if (leftValue == null && rightValue == null && !this.containsKey(key)) {
			return null;
		}
		
		return this.reducer.flatten((K)key, leftValue, rightValue);
	}
	
	@Override
	public Set<K> keySet() {
		return this.left.keySet();
	}

	@Override
	public Collection<ResultV> values() {
		return new InnerJoinValuesCollection();
	}

	protected class InnerJoinEntrySet extends AbstractSet<Map.Entry<K, ResultV>> {

		@Override
		public Iterator<Map.Entry<K, ResultV>> iterator() {
			return new Iterator<Map.Entry<K, ResultV>>() {
				private Map.Entry<K, ResultV> bufferedEntry = null;
				
				private final Iterator<Map.Entry<K, LeftV>> leftIterator = InnerJoinMap.this.left.entrySet().iterator();
				
				@Override
				public boolean hasNext() {
					if (this.bufferedEntry != null) {
						return true;
					} else {
						while (this.leftIterator.hasNext()) {
							Map.Entry<K, LeftV> leftEntry = this.leftIterator.next();
							K key = leftEntry.getKey();
							RightV rightValue = InnerJoinMap.this.right.get(key);
							if (rightValue != null ||  InnerJoinMap.this.right.containsKey(key)) {
								this.bufferedEntry = new AbstractMap.SimpleImmutableEntry<K, ResultV>(key, InnerJoinMap.this.reducer.flatten(key, leftEntry.getValue(), rightValue));
								break;
							}
						}
						
						return this.bufferedEntry != null;
					}
				}

				@Override
				public Map.Entry<K, ResultV> next() {
					if (this.hasNext()) {
						Map.Entry<K, ResultV> temp = this.bufferedEntry;
						this.bufferedEntry = null;
						return temp;
					} else {
						throw new NoSuchElementException();
					}
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			int size = 0;
			
			for (Iterator<?> i = this.iterator(); i.hasNext(); i.next()) {
				size++;
			}
			
			return size;
		}		
	}
	
	protected class InnerJoinValuesCollection extends AbstractCollection<ResultV> {
		@Override
		public Iterator<ResultV> iterator() {
			return new Iterator<ResultV>() {
				private ResultV bufferedResult = null;
				private boolean hasBufferedResult = false;
				
				private final Iterator<Map.Entry<K, LeftV>> leftIterator = InnerJoinMap.this.left.entrySet().iterator();
				
				@Override
				public boolean hasNext() {
					if (this.hasBufferedResult) {
						return true;
					} else {
						while (this.leftIterator.hasNext()) {
							Map.Entry<K, LeftV> leftEntry = this.leftIterator.next();
							K key = leftEntry.getKey();
							RightV rightValue = InnerJoinMap.this.right.get(key);
							if (rightValue != null ||  InnerJoinMap.this.right.containsKey(key)) {
								this.bufferedResult = InnerJoinMap.this.reducer.flatten(key, leftEntry.getValue(), rightValue);
								this.hasBufferedResult = true;
								break;
							}
						}
						
						return this.hasBufferedResult;
					}
				}

				@Override
				public ResultV next() {
					if (this.hasNext()) {
						ResultV temp = this.bufferedResult;
						this.bufferedResult = null;
						this.hasBufferedResult = false;
						return temp;
					} else {
						throw new NoSuchElementException();
					}
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			int size = 0;
			
			for (Iterator<?> i = this.iterator(); i.hasNext(); i.next()) {
				size++;
			}
			
			return size;
		}
	}
}
