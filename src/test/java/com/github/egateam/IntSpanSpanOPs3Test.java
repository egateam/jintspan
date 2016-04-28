/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntSpanSpanOPs3Test {

    @SuppressWarnings("CanBeFinal")
    private static class TestData {
        String runlist;
        int n;
        String expExcise;
        String expFill;

        TestData(String runlist, int n, String expExcise, String expFill) {
            this.runlist = runlist;
            this.n = n;
            this.expExcise = expExcise;
            this.expFill = expFill;
        }
    }

    private static final TestData[] tests =
        {
            new TestData("1-5", 1, "1-5", "1-5"),
            new TestData("1-5,7", 1, "1-5,7", "1-7"),
            new TestData("1-5,7", 2, "1-5", "1-7"),
            new TestData("1-5,7-8", 1, "1-5,7-8", "1-8"),
            new TestData("1-5,7-8", 3, "1-5", "1-8"),
            new TestData("1-5,7-8", 6, "-", "1-8"),
            new TestData("1-5,7,9-10", 0, "1-5,7,9-10", "1-5,7,9-10"),
            new TestData("1-5,9-10", 2, "1-5,9-10", "1-5,9-10"),
            new TestData("1-5,9-10", 3, "1-5", "1-10"),
            new TestData("1-5,9-10,12-13,15", 2, "1-5,9-10,12-13", "1-5,9-15"),
            new TestData("1-5,9-10,12-13,15", 3, "1-5", "1-15"),
        };

    @Test(description = "TestSpanOPs excise fill")
    public void testSpanOPs() {

        for ( TestData t : tests ) {
            Assert.assertEquals(new IntSpan(t.runlist).excise(t.n).toString(), t.expExcise,
                String.format("Test excise %s %d", t.runlist, t.n));

            Assert.assertEquals(new IntSpan(t.runlist).fill(t.n).toString(), t.expFill,
                String.format("Test fill %s %d", t.runlist, t.n));
        }
    }
}
