package com.davidehrmann.classifier4j;


public abstract class AbstractCategorizedTrainableClassifier<C,I> extends AbstractClassifier implements TrainableClassifier<C,I> {

    /**
     * @see Classifier#classify(java.lang.String)
     */
	
	protected final C defaultCategory;
	
	public AbstractCategorizedTrainableClassifier() {
		this(null);
	}
	
	public AbstractCategorizedTrainableClassifier(C defaultCategory) {
		this.defaultCategory = defaultCategory;
	}
	
    public double classify(I input) throws ClassifierException {
        return classify(this.defaultCategory, input);
    }

    public void teachMatch(I input) throws ClassifierException {
        teachMatch(this.defaultCategory, input);
    }

    public void teachNonMatch(I input) throws ClassifierException {
        teachNonMatch(this.defaultCategory, input);
    }

}
