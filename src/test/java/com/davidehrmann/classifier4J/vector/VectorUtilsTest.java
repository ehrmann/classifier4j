package com.davidehrmann.classifier4j.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class VectorUtilsTest {

	@Test
    public void testScalarProduct() {
        try {
            VectorUtils.scalarProduct(new int[] { 1, 2, 3 }, null);
            fail("Null argument allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        try {
            VectorUtils.scalarProduct(null, new int[] { 1, 2, 3 });
            fail("Null argument allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            VectorUtils.scalarProduct(new int[] {1}, new int[] { 1, 2, 3 });
            fail("Arguments of different size allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        assertEquals(3, VectorUtils.scalarProduct(new int[] {1,1,1}, new int[] { 1,1,1}));
        assertEquals(6, VectorUtils.scalarProduct(new int[] {1,1,1}, new int[] { 1,2,3}));
        assertEquals(14, VectorUtils.scalarProduct(new int[] {1,2,3}, new int[] { 1,2,3 }));
        assertEquals(0, VectorUtils.scalarProduct(new int[] {0,0,0}, new int[] { 1,2,3 }));

    }

	@Test
    public void testVectorLength() {
        try {
            VectorUtils.vectorLength((int[])null);
            fail("Null argument allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }        
    
        assertEquals(Math.sqrt(2), VectorUtils.vectorLength(new int[]{1,1}),0.001d);
        assertEquals(Math.sqrt(3), VectorUtils.vectorLength(new int[]{1,1,1}),0.001d);
        assertEquals(Math.sqrt(12), VectorUtils.vectorLength(new int[]{2,2,2}),0.001d);
    }

	@Test
    public void testCosineOfVectors() {
        try {
            VectorUtils.cosineOfVectors(new int[] { 1, 2, 3 }, null);
            fail("Null argument allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        try {
            VectorUtils.cosineOfVectors(null, new int[] { 1, 2, 3 });
            fail("Null argument allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        try {
            VectorUtils.cosineOfVectors(new int[] {1}, new int[] { 1, 2, 3 });
            fail("Arguments of different size allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }        
        
        int[] one = new int[]{1,1,1};
        int[] two = new int[]{1,1,1};
        
        assertEquals(1d, VectorUtils.cosineOfVectors(one, two), 0.001);
    }    
    
}