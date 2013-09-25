
package net.sf.classifier4J.vector;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class VectorUtils {
    public static int scalarProduct(int[] one, int[] two) throws IllegalArgumentException {
        if ((one == null) || (two == null)) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        if (one.length != two.length) {
            throw new IllegalArgumentException("Arguments of different length are not allowed");
        }
        
        int result = 0;
        for (int i = 0; i < one.length; i++) {
            result += one[i] * two[i];
        }
        return result;
    }
    
    public static double scalarProduct(Collection<IntegerTuple> v) throws IllegalArgumentException {

        int result = 0;
        for (IntegerTuple it : v) {
        	result += it.a * it.b;
        }

        return result;
    }
    
    public static int scalarProduct(List<Integer> one, List<Integer> two) throws IllegalArgumentException {
        if ((one == null) || (two == null)) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        if (one.size() != two.size()) {
            throw new IllegalArgumentException("Arguments of different length are not allowed");
        }
        
        int result = 0;
        Iterator<Integer> iterator1 = one.iterator();
        Iterator<Integer> iterator2 = two.iterator();
        
        while (iterator1.hasNext() && iterator2.hasNext()) {
        	result += iterator1.next() * iterator2.next();
        }
        return result;
    }
    
    public static double vectorLength(int[] vector) throws IllegalArgumentException {
        if (vector == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        double sumOfSquares = 0d;
        for (int i = 0; i < vector.length; i++) {
            sumOfSquares = sumOfSquares + (vector[i] * vector[i]);
        }
        
        return Math.sqrt(sumOfSquares);
    }
    
    public static double vectorLength(List<Integer> vector) throws IllegalArgumentException {
        if (vector == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        double sumOfSquares = 0d;
        
        for (Integer value : vector) {
            sumOfSquares = sumOfSquares + (value * value);
        }
        
        return Math.sqrt(sumOfSquares);
    }
    
    public static DoubleTuple vectorLength(Collection<IntegerTuple> vector) throws IllegalArgumentException {
        if (vector == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        double sumOfSquaresA = 0d;
        double sumOfSquaresB = 0d;
        
        for (IntegerTuple value : vector) {
            sumOfSquaresA = sumOfSquaresA + (value.a * value.a);
            sumOfSquaresB = sumOfSquaresB + (value.b * value.b);
        }
        
        return new DoubleTuple(Math.sqrt(sumOfSquaresA),  Math.sqrt(sumOfSquaresB));
    }
    
    
    
    public static double cosineOfVectors(int[] one, int[] two) throws IllegalArgumentException {
        if ((one == null) || (two == null)) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        if (one.length != two.length) {
            throw new IllegalArgumentException("Arguments of different length are not allowed");
        }     
        double denominater = (vectorLength(one) * vectorLength(two));
        if (denominater == 0) {
            return 0;
        } else {
            return (scalarProduct(one, two)/denominater);
        }
    }
    
    public static double cosineOfVectors(List<Integer> one, List<Integer> two) throws IllegalArgumentException {
        if ((one == null) || (two == null)) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        if (one.size() != two.size()) {
            throw new IllegalArgumentException("Arguments of different length are not allowed");
        }     
        double denominater = (vectorLength(one) * vectorLength(two));
        if (denominater == 0) {
            return 0;
        } else {
            return (scalarProduct(one, two)/denominater);
        }
    }
    
    public static double cosineOfVectors(Collection<IntegerTuple> vectors) throws IllegalArgumentException {

    	DoubleTuple t = vectorLength(vectors);
    	
        double denominater = t.a * t.b;
        if (denominater == 0) {
            return 0;
        } else {
            return (scalarProduct(vectors) / denominater);
        }
    }
    
    public static class IntegerTuple {
    	protected final int a;
    	protected final int b;
    	
    	public IntegerTuple(int a, int b) {
    		this.a = a;
    		this.b = b;
    	}
    }
    
    public static class DoubleTuple {
    	protected final double a;
    	protected final double b;
    	
    	public DoubleTuple(double a, double b) {
    		this.a = a;
    		this.b = b;
    	}
    }
}
