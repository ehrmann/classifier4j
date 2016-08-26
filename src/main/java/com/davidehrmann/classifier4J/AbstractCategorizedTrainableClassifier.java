package com.davidehrmann.classifier4j;

import com.davidehrmann.classifier4j.bayesian.WordsDataSourceException;


public abstract class AbstractCategorizedTrainableClassifier<C,I> extends AbstractClassifier<I> implements ITrainableClassifier<C,I> {

    /**
     * @see IClassifier#classify(java.lang.String)
     */
	
	protected final C defaultCategory;
	
	public AbstractCategorizedTrainableClassifier() {
		this(null);
	}
	
	public AbstractCategorizedTrainableClassifier(C defaultCategory) {
		this.defaultCategory = defaultCategory;
	}
	
    public double classify(I input) throws WordsDataSourceException, ClassifierException {
        return classify(this.defaultCategory, input);
    }

    public void teachMatch(I input) throws WordsDataSourceException, ClassifierException {
        teachMatch(this.defaultCategory, input);
    }

    public void teachNonMatch(I input) throws WordsDataSourceException, ClassifierException {
        teachNonMatch(this.defaultCategory, input);
    }

}
