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

import com.davidehrmann.classifier4j.*;
import com.davidehrmann.classifier4j.tokenizer.Tokenizer;
import com.davidehrmann.classifier4j.util.ToStringBuilder;

import java.util.*;

/**
 *
 * <p>A implementation of {@link Classifier} based on Bayes'
 * theorem (see http://www.wikipedia.org/wiki/Bayes_theorem).</p>
 *
 * <p>The basic usage pattern for this class is:
 * <ol>
 * 		<li>Create a instance of {@link WordsDataSource}</li>
 * 		<li>Create a new instance of BayesianClassifier, passing the IWordsDataSource
 * 			to the constructor</li>
 * 		<li>Call {@link Classifier#classify(I) }
 * 			or {@link Classifier#isMatch(I) }
 * </ol>
 * </p>
 *
 * <p>For example:<br>
 * <tt>
 * 		IWordsDataSource wds = new SimpleWordsDataSource();<br>
 * 		IClassifier classifier = new BayesianClassifier(wds);<br>
 * 		System.out.println( "Matches = " + classifier.classify("This is a sentence") );
 * </tt>
 * </p>
 *
 * @author Nick Lothian
 * @author Peter Leschev
 *
 */
public class BayesianClassifier<C,I,T> extends AbstractCategorizedTrainableClassifier<C,I> {

    protected final WordsDataSource<T,C> wordsData;
    protected final Tokenizer<I,T> tokenizer;
    protected final StopWordProvider<T> stopWordProvider;
    
    protected final boolean categorize;

    /**
     * Constructor for BayesianClassifier that specifies a datasource, tokenizer
     * and stop words provider
     *
     * @param wd a {@link WordsDataSource}
     * @param tokenizer a {@link Tokenizer}
     * @param swp a {@link StopWordProvider}
     */
    public BayesianClassifier(WordsDataSource<T,C> wd, Tokenizer<I,T> tokenizer, StopWordProvider<T> swp) {
        this.wordsData = Objects.requireNonNull(wd, "IWordsDataSource can't be null");
        this.tokenizer = Objects.requireNonNull(tokenizer, "ITokenizer can't be null");
        this.stopWordProvider = Objects.requireNonNull(swp, "IStopWordProvider can't be null");

        this.categorize = wordsData instanceof CategorizedWordsDataSource;
    }

    /**
     * @see CategorizedClassifier#isMatch(C, I)
     */
    public boolean isMatch(C category, I input) throws WordsDataSourceException {
        return isMatchWithTokens(category, Arrays.asList(tokenizer.tokenize(input)));
    }

    /**
     * @see CategorizedClassifier#classify(C, I)
     */
    public double classify(C category, I input) throws WordsDataSourceException {
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(input, "input cannot be null");
        checkCategoriesSupported(category);

        return classify(category, Arrays.asList(tokenizer.tokenize(input)));
    }

    public void teachMatch(C category, I input) throws WordsDataSourceException {
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(input, "input cannot be null");
        checkCategoriesSupported(category);

        teachMatchWithToken(category, Arrays.asList(tokenizer.tokenize(input)));
    }

    public void teachNonMatch(C category, I input) throws ClassifierException {
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(input, "input cannot be null");
        checkCategoriesSupported(category);

        teachNonMatchWithToken(category, Arrays.asList(tokenizer.tokenize(input)));
    }

    protected boolean isMatchWithTokens(C category, Collection<T> input) throws WordsDataSourceException {
        Objects.requireNonNull(input, "input cannot be null");
        checkCategoriesSupported(category);
        double matchProbability = classify(category, input);
        return matchProbability >= super.getMatchCutoff();
    }

    public double classify(C category, Collection<T> words) throws WordsDataSourceException {
        List<WordProbability<T,C>> wps = calcWordsProbability(category, words);
        return Classifier.normalizeSignificance(calculateOverallProbability(wps));
    }

    protected void teachMatchWithToken(C category, Collection<T> words) throws WordsDataSourceException {
        for (T word : words) {
            if (isClassifiableWord(word)) {
                if (categorize) {
                    ((CategorizedWordsDataSource<T,C>) wordsData).addMatch(category, word);
                } else {
                    wordsData.addMatch(word);
                }
            }
        }
    }

    protected void teachNonMatchWithToken(C category, Collection<T> words) throws WordsDataSourceException {
        for (T word : words) {
            if (isClassifiableWord(word)) {
                if (categorize) {
                    ((CategorizedWordsDataSource<T,C>) wordsData).addNonMatch(category, word);
                } else {
                    wordsData.addNonMatch(word);
                }
            }
        }
    }

    /**
     *
     * NOTE: Override this method with care. There is a good chance it will be removed
     * or have signature changes is later versions.
     *
     * <br />
     * @todo need an option to only use the "X" most "important" words when calculating overall probability
     * "important" is defined as being most distant from NEUTAL_PROBABILITY
     */
    protected double calculateOverallProbability(Collection<WordProbability<T,C>> wps) {
        if (wps == null || wps.isEmpty()) {
            return Classifier.NEUTRAL_PROBABILITY;
        } else {
            // we need to calculate xy/(xy + z)
            // where z = (1-x)(1-y)

            // firstly, calculate z and xy
            double z = 0d;
            double xy = 0d;
            for (WordProbability<T, C> wp : wps) {
                if (z == 0) {
                    z = (1 - wp.getProbability());
                } else {
                    z = z * (1 - wp.getProbability());
                }

                if (xy == 0) {
                    xy = wp.getProbability();
                } else {
                    xy = xy * wp.getProbability();
                }
            }

            double numerator = xy;
            double denominator = xy + z;

            return numerator / denominator;
        }
    }

	private List<WordProbability<T,C>> calcWordsProbability(C category, Collection<T> words) throws WordsDataSourceException {
        checkCategoriesSupported(category);

        List<WordProbability<T,C>> wps = new ArrayList<>();
        for (T word : words) {
            if (isClassifiableWord(word)) {
                WordProbability<T,C> wp;
                if (categorize) {
                    wp = ((CategorizedWordsDataSource<T,C>) wordsData).getWordProbability(category, word);
                } else {
                    wp = wordsData.getWordProbability(word);
                }
                if (wp != null) {
                    wps.add(wp);
                }
            }
        }
        return wps;
    }

    private void checkCategoriesSupported(C category) {
        // if the category is not the default
        if (category != null) {
            // and the data source does not support categories
            if (!categorize) {
                // throw an IllegalArgumentException
                throw new IllegalArgumentException("Word Data Source does not support non-default categories.");
            }
        }
    }

    private boolean isClassifiableWord(T word) {
        return !(word == null || stopWordProvider.isStopWord(word));
    }

    /**
     * @return the {@link WordsDataSource} used
     * by this classifier
     */
    public WordsDataSource<T,C> getWordsDataSource() {
        return wordsData;
    }

    /**
     * @return the {@link Tokenizer} used
     * by this classifier
     */
    public Tokenizer<I,T> getTokenizer() {
        return tokenizer;
    }

    /**
     * @return the {@link StopWordProvider} used
     * by this classifier
     */
    public StopWordProvider<T> getStopWordProvider() {
        return stopWordProvider;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("IWordsDataSource", wordsData)
                .append("ITokenizer", tokenizer)
                .append("IStopWordProvider", stopWordProvider)
                .toString();
    }

}
