/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntSpanSpanOPs1Test {

    @SuppressWarnings("CanBeFinal")
    private static class TestData {
        String runlist;
        String expCover;
        String expHoles;

        TestData(String runlist, String expCover, String expHoles) {
            this.runlist = runlist;
            this.expCover = expCover;
            this.expHoles = expHoles;
        }
    }

    private static final TestData[] tests =
        {
            new TestData("-", "-", "-"),
            new TestData("1", "1", "-"),
            new TestData("5", "5", "-"),
            new TestData("1,3,5", "1-5", "2,4"),
            new TestData("1,3-5", "1-5", "2"),
            new TestData("1-3,5,8-11", "1-11", "4,6-7"),

        };

    @Test(description = "TestSpanOPs cover holes")
    public void testSpanOPs() {

        for ( TestData t : tests ) {

            Assert.assertEquals(new IntSpan(t.runlist).cover().asString(), new IntSpan(t.expCover).asString(),
                String.format("Test cover %s %s", t.runlist, t.expCover));

            Assert.assertEquals(new IntSpan(t.runlist).holes().asString(), new IntSpan(t.expHoles).asString(),
                String.format("Test holes %s %s", t.runlist, t.expHoles));

        }
    }
}
