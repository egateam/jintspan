/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntSpanTestSpanOPs2 {

    @SuppressWarnings("CanBeFinal")
    private static class TestData {
        String runlist;
        int n;
        String expected;

        TestData(String runlist, int n, String expected) {
            this.runlist = runlist;
            this.n = n;
            this.expected = expected;
        }
    }

    private static final String universal = String.format("%d-%d", IntSpan.getNegInf(), IntSpan.getPosInf());

    private static final TestData[] tests =
        {
            new TestData("-", -2, "-"),
            new TestData("-", -1, "-"),
            new TestData("-", 0, "-"),
            new TestData("-", 1, "-"),
            new TestData("-", 2, "-"),

            new TestData(universal, -2, universal),
            new TestData(universal, 2, universal),

            new TestData(String.format("%d-0", IntSpan.getNegInf()), -2, String.format("%d-2", IntSpan.getNegInf())),
            new TestData(String.format("%d-0", IntSpan.getNegInf()), 2, String.format("%d--2", IntSpan.getNegInf())),

            new TestData(String.format("0-%d", IntSpan.getPosInf()), -2, String.format("-2-%d", IntSpan.getPosInf())),
            new TestData(String.format("0-%d", IntSpan.getPosInf()), 2, String.format("2-%d", IntSpan.getPosInf())),

            new TestData("0,2-3,6-8,12-15,20-24,30-35", -2, "-2-26,28-37"),
            new TestData("0,2-3,6-8,12-15,20-24,30-35", -1, "-1-9,11-16,19-25,29-36"),
            new TestData("0,2-3,6-8,12-15,20-24,30-35", 0, "0,2-3,6-8,12-15,20-24,30-35"),
            new TestData("0,2-3,6-8,12-15,20-24,30-35", 1, "7,13-14,21-23,31-34"),
            new TestData("0,2-3,6-8,12-15,20-24,30-35", 2, "22,32-33"),
        };

    @Test
    public void prompt() {
        System.out.println("TestSpanOPs inset pad trim");
    }

    @Test
    public void testSpanOPs() {

        for ( TestData t : tests ) {
            Assert.assertEquals(new IntSpan(t.runlist).inset(t.n).asString(), t.expected,
                String.format("Test inset %s %d", t.runlist, t.n));
        }

        Assert.assertEquals(new IntSpan("1-3").pad(1).size(), 5, "Test pad");
        Assert.assertEquals(new IntSpan("1-3").pad(2).size(), 7, "Test pad");
        Assert.assertEquals(new IntSpan("1-3").trim(1).size(), 1, "Test trim");
        Assert.assertEquals(new IntSpan("1-3").trim(2).size(), 0, "Test trim");
    }
}
