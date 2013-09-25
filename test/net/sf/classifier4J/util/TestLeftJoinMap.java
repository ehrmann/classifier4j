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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sf.classifier4J.util.LeftJoinMap.Reducer;

import org.junit.Test;

public class TestLeftJoinMap {

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
		
		Map<Integer, String> result = new LeftJoinMap<Integer, String, String, String>(left, right, new Reducer<Integer, String, String, String>() {
			@Override
			public String reduce(Integer key, String leftValue, String rightValue) {
				return rightValue != null ? leftValue + rightValue : null;
			}
		});
		
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
		
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(left, right, new Reducer<String, Integer, Integer, Integer>() {
			@Override
			public Integer reduce(String key, Integer leftValue, Integer rightValue) {
				return rightValue != null ? leftValue + rightValue : leftValue;
			}
		});
		
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
		
		Map<String, Integer> result = new LeftJoinMap<String, Integer, Integer, Integer>(left, right, new Reducer<String, Integer, Integer, Integer>() {
			@Override
			public Integer reduce(String key, Integer leftValue, Integer rightValue) {
				return rightValue != null ? leftValue + rightValue : leftValue;
			}
		});
		
		assertEquals(left.size(), result.size());
		assertEquals(expected, result);
		assertEquals(left.keySet(), result.keySet());
		assertEquals(new HashSet<Integer>(expected.values()), new HashSet<Integer>(result.values()));
	}

}
