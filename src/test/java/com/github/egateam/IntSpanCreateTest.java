/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntSpanCreateTest {

    @SuppressWarnings("CanBeFinal")
    private static class TestData {
        String input;
        String runlist;
        int[]  elements;

        TestData(String input, String runlist, int[] elements) {
            this.input = input;
            this.runlist = runlist;
            this.elements = elements;
        }
    }

    private static final TestData[] tests =
        {
            new TestData("", "-", new int[]{}),
            new TestData("-", "-", new int[]{}),
            new TestData("0", "0", new int[]{0}),
            new TestData("1", "1", new int[]{1}),
            new TestData("-1", "-1", new int[]{-1}),
            new TestData("1-2", "1-2", new int[]{1, 2}),
            new TestData("-2--1", "-2--1", new int[]{-2, -1}),
            new TestData("-2-1", "-2-1", new int[]{-2, -1, 0, 1}),
            new TestData("1,3-4", "1,3-4", new int[]{1, 3, 4}),

//            new TestData("1-1", "1", new int[]{1}),
//            new TestData("1,2-4", "1-4", new int[]{1, 2, 3, 4}),
//            new TestData("1-3,4,5-7", "1-7", new int[]{1, 2, 3, 4, 5, 6, 7}),
//            new TestData("1-3,4", "1-4", new int[]{1, 2, 3, 4}),
//            new TestData("1,2,3,4,5,6,7", "1-7", new int[]{1, 2, 3, 4, 5, 6, 7}),
        };

    @Test(description = "Test creations from runlist")
    public void testCreationRunlist() {

        for ( TestData t : tests ) {
            String message = "Test " + t.input;

            IntSpan set = new IntSpan(t.input);
            Assert.assertEquals(set.cardinality(), t.elements.length, message);
            Assert.assertEquals(set.toString(), t.runlist, message);
            Assert.assertEquals(set.toArray(), t.elements, message);

//            // rToR
//            if ( !t.runlist.isEmpty() && !t.runlist.equals("-") ) {
//                Assert.assertEquals(set.ranges(), IntSpan.rToR(t.runlist), message);
//            }

            // aliases
            IntSpan set1 = new IntSpan(set.copy());
            Assert.assertEquals(set.size(), t.elements.length, message);
            Assert.assertEquals(set.count(), t.elements.length, message);
            Assert.assertEquals(set1.runlist(), t.runlist, message);
            Assert.assertEquals(set1.elements(), t.elements, message);

        }
    }

    @Test(description = "Test creations from int")
    public void testCreationInt() {
        {
            String message = "Test int";

            IntSpan set = new IntSpan(1);

            String expectedString = "1";
            int[]  expectedArray  = new int[]{1};

            Assert.assertEquals(set.cardinality(), 1, message);
            Assert.assertEquals(set.toString(), expectedString, message);
            Assert.assertEquals(set.toArray(), expectedArray, message);
        }
    }

    @Test(description = "Test creations with error")
    public void testCreationError() {
        {
            try {
                IntSpan set = new IntSpan(1, -1);
                set.cardinality();
            } catch ( AssertionError err ) {
                System.out.println(err.getMessage());
                Assert.assertTrue(true, "Expected error");
            } catch ( Throwable err ) {
                Assert.assertTrue(false, "Doesn't catch error");
            }

            try {
                IntSpan set = new IntSpan("1--1");
                set.cardinality();
            } catch ( AssertionError err ) {
                System.out.println(err.getMessage());
                Assert.assertTrue(true, "Expected error");
            } catch ( Throwable err ) {
                Assert.assertTrue(false, "Doesn't catch error");
            }

            try {
                IntSpan set = new IntSpan("1-1--1");
                set.cardinality();
            } catch ( AssertionError err ) {
                System.out.println(err.getMessage());
                Assert.assertTrue(true, "Expected error");
            } catch ( Throwable err ) {
                Assert.assertTrue(false, "Doesn't catch error");
            }

            try {
                IntSpan set = new IntSpan("abc");
                set.cardinality();
            } catch ( AssertionError err ) {
                System.out.println(err.getMessage());
                Assert.assertTrue(true, "Expected error");
            } catch ( NumberFormatException err ) {
                System.out.println(err.getMessage());
                Assert.assertTrue(true, "Expected parseInt error");
            } catch ( Throwable err ) {
                Assert.assertTrue(false, "Doesn't catch error");
            }
        }
    }
}
