/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Nick Lothian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        developers of Classifier4J (http://classifier4j.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "Classifier4J" must not be used to endorse or promote 
 *    products derived from this software without prior written 
 *    permission. For written permission, please contact   
 *    http://sourceforge.net/users/nicklothian/.
 *
 * 5. Products derived from this software may not be called 
 *    "Classifier4J", nor may "Classifier4J" appear in their names 
 *    without prior written permission. For written permission, please 
 *    contact http://sourceforge.net/users/nicklothian/.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */


package com.davidehrmann.classifier4j.bayesian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.davidehrmann.classifier4j.DefaultStopWordsProvider;
import com.davidehrmann.classifier4j.tokenizer.SimpleStringTokenizer;
import com.davidehrmann.classifier4j.Classifier;
import com.davidehrmann.classifier4j.StopWordProvider;
import com.davidehrmann.classifier4j.tokenizer.Tokenizer;

import org.junit.Test;

/*
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class BayesianClassifierTest {

	protected final SimpleWordsDataSource<String, Integer> wds = new SimpleWordsDataSource<String, Integer>();
	protected final Tokenizer<String> tokenizer = new SimpleStringTokenizer();
	protected final StopWordProvider<String> stopWordProvider = new DefaultStopWordsProvider();
	protected final BayesianClassifier<Integer,String> classifier = new BayesianClassifier<Integer,String>(wds, tokenizer, stopWordProvider);


	@Test
	public void testClassify() throws Exception {

		String sentence[] = { "This", "is", "a", "sentence", "about", "java" };

		assertEquals(Classifier.NEUTRAL_PROBABILITY, classifier.classify(null, sentence), 0d);

		wds.setWordProbability(new WordProbability<String, Integer>("This", 0.5d));
		wds.setWordProbability(new WordProbability<String, Integer>("is", 0.5d));
		wds.setWordProbability(new WordProbability<String, Integer>("a", 0.5d));
		wds.setWordProbability(new WordProbability<String, Integer>("sentence", 0.2d));
		wds.setWordProbability(new WordProbability<String, Integer>("about", 0.5d));
		wds.setWordProbability(new WordProbability<String, Integer>("java", 0.99d));

		assertEquals(0.96d, classifier.classify(null, sentence), 0.009d);
	}

	@Test
	public void testTeaching() throws Exception {
		String sentence1[] = {"The", "menu", "tag", "library", "manages", "the", 
				"complex", "process", "of", "creating", "menus", "in",
				"JavaScript", "The", "menu", "tag", "itself", "is", 
				"an", "abstract", "class", "that", "extends", "the", 
				"TagSupport", "class", "and", "overrides", "the", 
				"doStartTag", "and", "doEndTag", "methods.", "The", 
				"getMenu", "method,", "which", "is", "a", "template", 
				"method", "and", "should", "be", "overridden", "in", 
				"the", "subclasses,", "provides", "JavaScript", "to", 
				"add", "menu", "items", "in", "the", "menu", 
				"structure", "created", "in", "the", "doStartTag", 
				"method", "Subclasses", "of", "the", "menu", "tag", 
				"override", "the", "getMenu", "method,", "which", 
				"uses", "menu", "builders", "to", "render", "menu", 
				"data", "from", "the", "data", "source"};

		String sentence2[] = {"I", "witness", "a", "more", "subtle", 
				"demonstration", "of", "real", "time", "physics", 
				"simulation", "at", "the", "tiny", "Palo", "Alto", 
				"office", "of", "Havok", "a", "competing", "physics", 
				"engine", "shop", "On", "the", "screen", "a", 
				"computer", "generated", "sailboat", "floats", "in", 
				"a", "stone", "lined", "pool", "of", "water", "The", 
				"company's", "genial", "Irish", "born", "cofounder", 
				"Hugh", "Reynolds", "shows", "me", "how", "to", 
				"push", "the", "boat", "with", "a", "mouse", "When", 
				"I", "nudge", "it", "air", "fills", "the", "sail", 
				"causing", "the", "ship", "to", "tilt", "leeward", 
				"Ripples", "in", "the", "water", "deflect", "off", 
				"the", "stones", "intersecting", "with", "one", 
				"another", "I", "urge", "the", "boat", "onward", 
				"and", "it", "glides", "effortlessly", "into", "the", 
				"wall", "Reynolds", "tosses", "in", "a", "handful", 
				"of", "virtual", "coins", "they", "spin", "through", 
				"the", "air,", "splash", "into", "the", "water,", 
				"and", "sink"};

		String sentence3[] = {"The", "New", "Input", "Output", "NIO", "libraries", 
				"introduced", "in", "Java", "2", "Platform", 
				"Standard", "Edition", "J2SE", "1.4", "address", 
				"this", "problem", "NIO", "uses", "a", "buffer", 
				"oriented", "model", "That", "is", "NIO", "deals", 
				"with", "data", "primarily", "in", "large", "blocks", 
				"This", "eliminates", "the", "overhead", "caused", 
				"by", "the", "stream", "model", "and", "even", "makes",
				"use", "of", "OS", "level", "facilities", "where", 
				"possible", "to", "maximize", "throughput"};

		String sentence4[] = {"As", "governments", "scramble", "to", "contain", 
				"SARS", "the", "World", "Health", "Organisation", 
				"said", "it", "was", "extending", "the", "scope", "of",
				"its", "April", "2", "travel", "alert", "to", 
				"include", "Beijing", "and", "the", "northern", 
				"Chinese", "province", "of", "Shanxi", "together", 
				"with", "Toronto", "the", "epicentre", "of", "the", 
				"SARS", "outbreak", "in", "Canada"};

		String sentence5[] = {"That", "was", "our", "worst", "problem", "I", 
				"tried", "to", "see", "it", "the", "XP", "way", "Well",
				"what", "we", "can", "do", "is", "implement", 
				"something", "I", "can't", "give", "any", "guarantees",
				"as", "to", "how", "much", "of", "it", "will", "be", 
				"implemented", "in", "a", "month", "I", "won't", 
				"even", "hazard", "a", "guess", "as", "to", "how", 
				"long", "it", "would", "take", "to", "implement", "as",
				"a", "whole", "I", "can't", "draw", "UML", "diagrams", 
				"for", "it", "or", "write", "technical", "specs", 
				"that", "would", "take", "time", "from", "coding", 
				"it", "which", "we", "can't", "afford", "Oh", "and", 
				"I", "have", "two", "kids", "I", "can't", "do", "much",
				"OverTime", "But", "I", "should", "be", "able", "to", 
				"do", "something", "simple", "that", "will", "have", 
				"very", "few", "bugs", "and", "show", "a", "working", 
				"program", "early", "and", "often"}; 		


		classifier.teachMatch(null, sentence1);
		classifier.teachNonMatch(null, sentence2);
		classifier.teachMatch(null, sentence3);
		classifier.teachNonMatch(null, sentence4);
		classifier.teachMatch(null, sentence5);

		assertTrue(classifier.isMatch(null, sentence1));
		assertFalse(classifier.isMatch(null, sentence2));
		assertTrue(classifier.isMatch(null, sentence3));
		assertFalse(classifier.isMatch(null, sentence4));
		assertTrue(classifier.isMatch(null, sentence5));
	}

	@Test
	public void testGetWordsDataSource() throws Exception {
		assertEquals(wds, classifier.getWordsDataSource());
	}

	@Test
	public void testGetTokenizer() throws Exception {
		assertEquals(tokenizer, classifier.getTokenizer());
	}

	@Test
	public void testGetStopWordProvider() throws Exception {
		assertEquals(stopWordProvider, classifier.getStopWordProvider());		
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCalculateOverallProbability() throws Exception {
		double prob = 0.3d;

		@SuppressWarnings("rawtypes")
		WordProbability[] wps = {
				new WordProbability<String,Integer>("myWord1", prob),
				new WordProbability<String,Integer>("myWord2", prob),
				new WordProbability<String,Integer>("myWord3", prob),
		};

		double errorMargin = 0.0001d;

		double xy = (prob * prob * prob);
		double z = (1-prob)*(1-prob)*(1-prob);

		double result = xy/(xy + z);

		assertEquals(result, classifier.calculateOverallProbability(wps), errorMargin);
	}
}
