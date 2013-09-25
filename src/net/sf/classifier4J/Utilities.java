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

package net.sf.classifier4J;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class Utilities {

    public static Map<String, Integer> getWordFrequency(String input) {
        return getWordFrequency(input, false);
    }

    public static Map<String, Integer> getWordFrequency(String input, boolean caseSensitive) {
        return getWordFrequency(input, new DefaultTokenizer(), new DefaultStopWordsProvider());
    }

    /**
     * Get a Map of words and Integer representing the number of each word
     * 
     * @param input The String to get the word frequency of
     * @param caseSensitive true if words should be treated as separate if they have different case
     * @param tokenizer a junit.framework.TestCase#run()
     * @param stopWordsProvider
     * @return
     */
    public static <I> Map<I,Integer> getWordFrequency(I input, ITokenizer<I> tokenizer, IStopWordProvider<I> stopWordsProvider) {
        // tokenize into an array of words
        I[] words = tokenizer.tokenize(input);
        
        Map<I,Integer> result = new HashMap<I,Integer>();
        
        for (I word : words) {
        	// If there is a stop word provider, add only non-stop words
        	if (stopWordsProvider == null || stopWordsProvider.isStopWord(word) == false) {
        		Integer count = result.get(word);
        		if (count == null) {
        			result.put(word, 1);
        		} else {
        			result.put(word, count + 1);
        		}
        	}
        }

        return result;
    }

    @SuppressWarnings("unchecked")
	private static <I> I[] findWordsWithFrequency(Map<I,Integer> wordFrequencies, Integer frequency) {
    	List<I> results = new ArrayList<I>();
    	
        if (wordFrequencies != null && frequency != null) {            
            for (Entry<I,Integer> entry : wordFrequencies.entrySet()) {
                if (frequency.equals(entry.getValue())) {
                    results.add(entry.getKey());
                }
            }
        }
        
        return (I[]) results.toArray();
    }    
    
    public static <I> Set<I> getMostFrequentWords(int count, Map<I, Integer> wordFrequencies) {
        Set<I> result = new LinkedHashSet<I>();

        Integer max = Collections.max(wordFrequencies.values());

        int freq = max;
        while (result.size() < count && freq > 0) {
            // this is very icky
            I words[] = findWordsWithFrequency(wordFrequencies, new Integer(freq));
            result.addAll(Arrays.asList(words));
            freq--;
        }

        return result;
    }

    /**
     * Find all unique words in an array of words
     * 
     * @param input an array of Strings
     * @return an array of all unique strings. Order is not guaranteed
     */
    @SuppressWarnings("unchecked")
	public static <I> I[] getUniqueWords(I[] input) {
    	Set<I> results = new LinkedHashSet<I>();
    	
    	if (input != null) {
            for (int i = 0; i < input.length; i++) {
            	results.add(input[i]);
            }
    	}
    	
        return (I[]) results.toArray();
    }

    /**
     * Count how many times a word appears in an array of words
     * 
     * @param word The word to count
     * @param words non-null array of words 
     */
    public static int countWords(String word, String[] words) {
        // find the index of one of the items in the array.
        // From the JDK docs on binarySearch:
        // If the array contains multiple elements equal to the specified object, there is no guarantee which one will be found. 
        int itemIndex = Arrays.binarySearch(words, word);

        // iterate backwards until we find the first match
        if (itemIndex > 0) {
            while (itemIndex > 0 && words[itemIndex].equals(word)) {
                itemIndex--;
            }
        }

        // now itemIndex is one item before the start of the words
        int count = 0;
        while (itemIndex < words.length && itemIndex >= 0) {
            if (words[itemIndex].equals(word)) {
                count++;
            }

            itemIndex++;
            if (itemIndex < words.length) {
                if (!words[itemIndex].equals(word)) {
                    break;
                }
            }
        }

        return count;
    }

    /**
     * 
     * @param input a String which may contain many sentences
     * @return an array of Strings, each element containing a sentence
     */
    public static String[] getSentences(String input) {
        if (input == null) {
            return new String[0];
        } else {
            // split on a ".", a "!", a "?" followed by a space or EOL
            return input.split("(\\.|!|\\?)+(\\s|\\z)");
        }

    }

    /**
     * Given an inputStream, this method returns a String. New lines are 
     * replaced with " "
     */
    public static String getString(InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = "";
        StringBuffer stringBuffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append(" ");
        }

        reader.close();

        return stringBuffer.toString().trim();
    }
}
