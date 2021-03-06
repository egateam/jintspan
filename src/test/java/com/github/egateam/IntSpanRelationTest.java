/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("CanBeFinal")
public class IntSpanRelationTest {

    private static String[] sets =
        {
            "-", "1", "5", "1-5", "3-7", "1-3,8,10-23"
        };

    private static int[][] equals =
        {
            {1, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 1},
        };

    private static int[][] subset =
        {
            {1, 1, 1, 1, 1, 1},
            {0, 1, 0, 1, 0, 1},
            {0, 0, 1, 1, 1, 0},
            {0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 1},
        };

    private static int[][] superset =
        {
            {1, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0, 0},
            {1, 0, 1, 0, 0, 0},
            {1, 1, 1, 1, 0, 0},
            {1, 0, 1, 0, 1, 0},
            {1, 1, 0, 0, 0, 1},
        };

    @Test(description = "Test relations from runlist")
    public void testRelation() {
        for ( int i = 0; i < sets.length; i++ ) {
            for ( int j = 0; j < sets.length; j++ ) {
                String message = "Test " + i + " " + j;

                IntSpan A = new IntSpan(sets[i]);
                IntSpan B = new IntSpan(sets[j]);

                // equals
                Assert.assertEquals(A.equals(B), equals[i][j] != 0, message + " equals");
                Assert.assertEquals(A.equal(B), equals[i][j] != 0, message + " equals");

                // subset
                Assert.assertEquals(A.subset(B), subset[i][j] != 0, message + " subset");

                // superset
                Assert.assertEquals(A.superset(B), superset[i][j] != 0, message + " superset");

            }
        }

    }
}
