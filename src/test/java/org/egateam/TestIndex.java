/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package org.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestIndex {

    @SuppressWarnings("CanBeFinal")
    private static class TestData {
        String runlist;
        int number;
        Integer expIndex;
        Integer expElement;

        TestData(String runlist, int number, Integer expIndex, Integer expElement) {
            this.runlist = runlist;
            this.number = number;
            this.expIndex = expIndex;
            this.expElement = expElement;
        }
    }

    private static final TestData[] tests =
        {
            new TestData("-", 1, null, null),
            new TestData("-", -1, null, null),
            new TestData("1-10,21-30", 25, null, 15),
            new TestData("1-10,21-30", -25, null, null),

            new TestData("0-9", 1, 0, 2),
            new TestData("0-9", 6, 5, 7),
            new TestData("0-9", 10, 9, null),
            new TestData("0-9", 11, null, null),

            new TestData("0-9", -1, 9, null),
            new TestData("0-9", -5, 5, null),
            new TestData("0-9", -10, 0, null),
            new TestData("0-9", -11, null, null),

            new TestData("1-10,21-30,41-50", 6, 6, 6),
            new TestData("1-10,21-30,41-50", 16, 26, null),
            new TestData("1-10,21-30,41-50", 26, 46, 16),
            new TestData("1-10,21-30,41-50", 31, null, null),

            new TestData("1-10,21-30,41-50", -1, 50, null),
            new TestData("1-10,21-30,41-50", -11, 30, null),
            new TestData("1-10,21-30,41-50", -21, 10, null),
            new TestData("1-10,21-30,41-50", -30, 1, null),
            new TestData("1-10,21-30,41-50", -31, null, null),
        };

    @Test
    public void prompt() {
        System.out.println("TestIndex");
    }

    @Test
    public void testIndex() {

        for ( TestData t : tests ) {

            if ( t.expIndex != null ) {
                String message = String.format("Test at %s %d %d", t.runlist, t.number, t.expIndex);

                IntSpan set = new IntSpan(t.runlist);
                int expected = t.expIndex;
                Assert.assertEquals(set.at(t.number), expected, message);
            } else {
                try {
                    IntSpan set = new IntSpan(t.runlist);
                    set.at(t.number);
                } catch ( AssertionError err ) {
                    System.out.println(err.getMessage());
                    Assert.assertTrue(true, "Expected error");
                } catch ( Throwable err ) {
                    Assert.assertTrue(false, "Doesn't catch error");
                }
            }

            if ( t.expElement != null ) {
                String message = String.format("Test index %s %d %d", t.runlist, t.number, t.expElement);
                IntSpan set = new IntSpan(t.runlist);
                int expected = t.expElement;
                Assert.assertEquals(set.index(t.number), expected, message);
            } else {
                try {
                    IntSpan set = new IntSpan(t.runlist);
                    set.index(t.number);
                } catch ( AssertionError err ) {
                    System.out.println(err.getMessage());
                    Assert.assertTrue(true, "Expected error");
                } catch ( Throwable err ) {
                    Assert.assertTrue(false, "Doesn't catch error");
                }
            }

        }
    }
}
