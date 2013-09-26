
package net.sf.classifier4J.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;

import net.sf.classifier4J.ClassifierException;
import net.sf.classifier4J.DefaultStopWordsProvider;
import net.sf.classifier4J.SimpleStringTokenizer;

import org.junit.Test;


public class VectorClassifierTest {

	protected static final String sentence1 = "hello there is this a long sentence yes it is blah. blah hello";

	/*
	 * Class under test for double classify(String, String)
	 */
	@Test
	public void testClassifyStringString() throws ClassifierException {
		TermVectorStorage<Integer, String> storage = new HashMapTermVectorStorage<Integer,String>();
		VectorClassifier<Integer, String> vc = new VectorClassifier<Integer, String>(storage, new SimpleStringTokenizer(), new DefaultStopWordsProvider());

		Integer category = 0xCA71;
		vc.teachMatch(category, sentence1);
		assertEquals(0.852d, vc.classify(category, "hello blah"), 0.001);
		assertEquals(0.301d, vc.classify(category, "sentence"), 0.001);
		assertEquals(0.0d, vc.classify(category, "bye"), 0.001);

		assertEquals(0.0d, vc.classify(0, "bye"), 0.001);         
	}

	/*
	 * Class under test for boolean isMatch(String, String)
	 */
	@Test
	public void testIsMatchStringString() throws ClassifierException {
		TermVectorStorage<String, String> storage = new HashMapTermVectorStorage<String, String>();
		VectorClassifier<String, String> vc = new VectorClassifier<String, String>(storage, new SimpleStringTokenizer(), new DefaultStopWordsProvider());

		String category = "test";
		vc.teachMatch(category, sentence1);
		assertTrue(vc.isMatch(category, "hello blah"));
		assertFalse(vc.isMatch(category, "sentence"));
		assertFalse(vc.isMatch(category, "bye"));           
	}

	/*
	 * Class under test for void teachMatch(String, String)
	 */
	@Test
	public void testTeachMatchStringString() throws ClassifierException {
		TermVectorStorage<String, String> storage = new HashMapTermVectorStorage<String, String>();
		VectorClassifier<String, String> vc = new VectorClassifier<String, String>(storage, new SimpleStringTokenizer(), new DefaultStopWordsProvider());

		String category = "test";
		vc.teachMatch(category, sentence1);
		TermVector<String> tv = storage.getTermVector(category);
		assertNotNull("TermVector should not be null", tv);

		HashMap<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("blah", 2);
		expected.put("hello", 2);
		expected.put("long", 1);
		expected.put("sentence", 1);
		expected.put("yes", 1);
		
		assertEquals(expected.size(), tv.getTerms().size());
		assertEquals(tv.getValues().size(), tv.getTerms().size());
		
		Iterator<String> termsIterator = tv.getTerms().iterator();
		Iterator<Integer> valuesIterator = tv.getValues().iterator();
		
		while (termsIterator.hasNext() && valuesIterator.hasNext()) {
			assertEquals(expected.get(termsIterator.next()), valuesIterator.next());
		}
	}
}
