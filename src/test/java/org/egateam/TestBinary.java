/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package org.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestBinary {

    @SuppressWarnings("CanBeFinal")
    private static class TestData {
        String A;
        String B;
        String U;
        String I;
        String X;
        String AB;
        String BA;

        TestData(String A,
                 String B,
                 String U,
                 String I,
                 String X,
                 String AB,
                 String BA) {
            this.A = A;
            this.B = B;
            this.U = U;
            this.I = I;
            this.X = X;
            this.AB = AB;
            this.BA = BA;
        }
    }

    private static final TestData[] tests =
        {
            //            A             B        U           I        X            A-B     B-A
            new TestData("-         ", "-    ", "-       ", "-    ", "-        ", "-   ", "-    "),
            new TestData("1         ", "1    ", "1       ", "1    ", "-        ", "-   ", "-    "),
            new TestData("1         ", "2    ", "1-2     ", "-    ", "1-2      ", "1   ", "2    "),
            new TestData("3-9       ", "1-2  ", "1-9     ", "-    ", "1-9      ", "3-9 ", "1-2  "),
            new TestData("3-9       ", "1-5  ", "1-9     ", "3-5  ", "1-2,6-9  ", "6-9 ", "1-2  "),
            new TestData("3-9       ", "4-8  ", "3-9     ", "4-8  ", "3,9      ", "3,9 ", " -   "),
            new TestData("3-9       ", "5-12 ", "3-12    ", "5-9  ", "3-4,10-12", "3-4 ", "10-12"),
            new TestData("3-9       ", "10-12", "3-12    ", "-    ", "3-12     ", "3-9 ", "10-12"),
            new TestData("1-3,5,8-11", "1-6  ", "1-6,8-11", "1-3,5", "4,6,8-11 ", "8-11", "4,6  "),
        };

    @Test
    public void prompt() {
        System.out.println("Test binary ops");
    }

    @Test
    public void testBinary() {

        for ( TestData t : tests ) {
            String message = "Test " + t.A + " " + t.B;
            IntSpan A = new IntSpan(t.A);
            IntSpan B = new IntSpan(t.B);

            // union
            Assert.assertEquals(A.union(B).asArray(), new IntSpan(t.U).asArray(), message + " union");

            // intersect
            Assert.assertEquals(A.intersect(B).asArray(), new IntSpan(t.I).asArray(), message + " intersect");

            // xor
            Assert.assertEquals(A.xor(B).asArray(), new IntSpan(t.X).asArray(), message + " xor");

            // diff A-B
            Assert.assertEquals(A.diff(B).asArray(), new IntSpan(t.AB).asArray(), message + " diff A-B");

            // diff B-A
            Assert.assertEquals(B.diff(A).asArray(), new IntSpan(t.BA).asArray(), message + " diff B-A");
        }
    }
}
