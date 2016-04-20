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
    private static class TestDataAt {
        String runlist;
        int index;
        Integer expected;

        TestDataAt(String runlist, int index, Integer expected) {
            this.runlist = runlist;
            this.index = index;
            this.expected = expected;
        }
    }

    private static final TestDataAt[] tests =
        {
            new TestDataAt("-", 1, null),
            new TestDataAt("-", -1, null),
            new TestDataAt("1-10,20-30", 25, null),
            new TestDataAt("1-10,20-30", -25, null),

            new TestDataAt("0-9", 1, 0),
            new TestDataAt("0-9", 6, 5),
            new TestDataAt("0-9", 10, 9),
            new TestDataAt("0-9", 11, null),

            new TestDataAt("0-9", -1, 9),
            new TestDataAt("0-9", -5, 5),
            new TestDataAt("0-9", -10, 0),
            new TestDataAt("0-9", -11, null),

            new TestDataAt("1-10,21-30,41-50", 6, 6),
            new TestDataAt("1-10,21-30,41-50", 16, 26),
            new TestDataAt("1-10,21-30,41-50", 26, 46),
            new TestDataAt("1-10,21-30,41-50", 31, null),

            new TestDataAt("1-10,21-30,41-50", -1, 50),
            new TestDataAt("1-10,21-30,41-50", -11, 30),
            new TestDataAt("1-10,21-30,41-50", -21, 10),
            new TestDataAt("1-10,21-30,41-50", -30, 1),
            new TestDataAt("1-10,21-30,41-50", -31, null),

        };

    @Test
    public void prompt() {
        System.out.println("TestIndex");
    }

    @Test
    public void testCreationRunlist() {

        for ( TestDataAt t : tests ) {

            if ( t.expected != null ) {
                String message = String.format("Test %s %d %d", t.runlist, t.index, t.expected);

                IntSpan set = new IntSpan(t.runlist);
                int expected = t.expected;
                Assert.assertEquals(set.at(t.index), expected, message);
            } else {
                try {
                    IntSpan set = new IntSpan(t.runlist);
                    set.at(t.index);
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
