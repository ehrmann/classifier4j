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

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class TestLeftJoinMap {

	protected static <K> LeftJoinMap.Reducer<K, String, String, String> concatenatingReducer() {
		return new LeftJoinMap.Reducer<K, String, String, String>() {
			@Override
			public String reduce(K key, String leftValue, String rightValue) {
				return rightValue != null ? leftValue + rightValue : null;
			}
		};
	}
		
	protected static <K> LeftJoinMap.Reducer<K, Integer, Integer, Integer> addingReducer() {
		return new LeftJoinMap.Reducer<K, Integer, Integer, Integer>() {
			@Override
			public Integer reduce(K key, Integer leftValue, Integer rightValue) {
				return rightValue != null ? leftValue + rightValue : leftValue;
			}
		};
	}
	
	@Test
	public void test() {
		
		Map<Integer, String> left = new HashMap<Integer, String>();
		left.put(0, "ze");
		left.put(1, "on");
		left.put(2, "t");
		left.put(3, "th");
		left.put(4, "fo");
		
		Map<Integer, String> right = new HashMap<Integer, String>();
		right.put(0, "ro");
		right.put(2, "wo");
		right.put(4, "ur");
		
		Map<Integer, String> expected = new HashMap<Integer, String>();
		expected.put(0, "zero");
		expected.put(1, null);
		expected.put(2, "two");
		expected.put(3, null);
		expected.put(4, "four");
		
		Map<Integer, String> result = new LeftJoinMap<Integer, String, String, String>(left, right, TestLeftJoinMap.<Integer>concatenatingReducer());
		
		assertEquals(left.size(), result.size());
		assertEquals(expected, result);
		assertEquals(left.keySet(), result.keySet());
		assertEquals(new HashSet<String>(expected.values()), new HashSet<String>(result.values()));
	}
	
	@Test
	public void test2() {
		
		Map<String, Integer> left = new HashMap<String, Integer>();
		left.put("a", 10);
		left.put("b", 85);
		left.put("c", 12);
		left.put("d", 75);
		left.put("e", 6);
		left.put("f", 44);
		left.put("g", 62);
		left.put("h", 9);
		left.put("i", 34);
		left.put("j", 61);
		left.put("k", 96);
		left.put("l", 54);
		left.put("m", 30);
		
		Map<String, Integer> right = new HashMap<String, Integer>();
		right.put("b", 42);
		right.put("c", 96);
		right.put("d", 12);
		right.put("f", 45);
		right.put("g", 33);
		right.put("j", 52);
		right.put("k", 47);
		right.put("l", 23);
		
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("a", 10);
		expected.put("b", 127);
		expected.put("c", 108);
		expected.put("d", 87);
		expected.put("e", 6);
		expected.put("f", 89);
		expected.put("g", 95);
		expected.put("h", 9);
		expected.put("i", 34);
		expected.put("j", 113);
		expected.put("k", 143);
		expected.put("l", 77);
		expected.put("m", 30);
		
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(left, right, TestLeftJoinMap.<String>addingReducer());
		
		assertEquals(left.size(), result.size());
		assertEquals(expected, result);
		assertEquals(left.keySet(), result.keySet());
		assertEquals(new HashSet<Integer>(expected.values()), new HashSet<Integer>(result.values()));
	}
	
	@Test
	public void testNullKeyJoin() {
		Map<String, Integer> left = new HashMap<String, Integer>();
		left.put("a", 10);
		left.put(null, 85);
		
		Map<String, Integer> right = new HashMap<String, Integer>();
		right.put("b", 42);
		right.put("c", 96);
		right.put("d", 12);
		right.put(null, 3);
		
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("a", 10);
		expected.put(null, 88);
		
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(left, right, TestLeftJoinMap.<String>addingReducer());
		
		assertEquals(left.size(), result.size());
		assertEquals(expected, result);
		assertEquals(left.keySet(), result.keySet());
		assertEquals(new HashSet<Integer>(expected.values()), new HashSet<Integer>(result.values()));
	}

	@Test
	public void testJoinEmptyRight() {
		Map<String, Integer> left = new HashMap<String, Integer>();
		left.put("b", 42);
		left.put("c", 96);
		left.put("d", 12);
		left.put(null, 3);
		
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(left, Collections.<String, Integer>emptyMap(), TestLeftJoinMap.<String>addingReducer());
		
		assertEquals(left, result);
	}
	
	@Test
	public void testJoinEmptyLeft() {
		Map<String, Integer> right = new HashMap<String, Integer>();
		right.put("b", 42);
		right.put(null, 3);
		
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.<String, Integer>emptyMap(), right, TestLeftJoinMap.<String>addingReducer());
		
		assertEquals(Collections.<String, Integer>emptyMap(), result);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testRemove() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.remove(key);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testPut() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.put("b", 10);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUpdate() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.put("b", 10);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testClear() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.clear();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testEntrySetAdd() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.entrySet().add(new Entry<String, Integer>() {
			@Override public Integer setValue(Integer arg0) {
				return null;
			}
			
			@Override public Integer getValue() {
				return null;
			}
			
			@Override public String getKey() {
				return null;
			}
		});
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testEntrySetRemove() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.entrySet().remove(result.entrySet().iterator().next());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testEntrySetIteratorRemove() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
		iterator.next();
		iterator.remove();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testEntrySetEntryUpdate() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
		iterator.next().setValue(0);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testEntrySetClear() {
		String key = "a";
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(Collections.singletonMap(key, 10), Collections.singletonMap(key, 12), TestLeftJoinMap.<String>addingReducer());
		result.entrySet().clear();
	}
}
