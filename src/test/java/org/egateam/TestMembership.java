/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package org.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("CanBeFinal")
public class TestMembership {

    private static String[] sets = {"-", "1", "1-2", "1,3-5"};

    private static int[][] contains =
        {
            //1, 2, 3, 4
            {0, 0, 0, 0},
            {1, 0, 0, 0},
            {1, 1, 0, 0},
            {1, 0, 1, 1},
        };

    private static String[][] added =
        {
            {"1    ", "2    ", "3    ", "4    "},
            {"1    ", "1-2  ", "1,3  ", "1,4  "},
            {"1-2  ", "1-2  ", "1-3  ", "1-2,4"},
            {"1,3-5", "1-5  ", "1,3-5", "1,3-5"},
        };

    private static String[][] removed =
        {
            {"-    ", "-    ", "-    ", "-    "},
            {"-    ", "1    ", "1    ", "1    "},
            {"2    ", "1    ", "1-2  ", "1-2  "},
            {"3-5  ", "1,3-5", "1,4-5", "1,3,5"},
        };

    @Test
    public void prompt() {
        System.out.println("TestMembership");
    }

    @Test
    public void testMembership() {
        for ( int i = 0; i < sets.length; i++ ) {
            for ( int j = 0; j < sets.length; j++ ) {
                String message = "Test " + i + " " + j;

                int n = j + 1;

                IntSpan A = new IntSpan(sets[i]);

                // contains
                Assert.assertEquals(A.contains(n), contains[i][j] != 0, message + " contains");

                // added
                Assert.assertEquals(A.add(n).asString(), new IntSpan(added[i][j]).asString(), message + " added");
                Assert.assertTrue(A.containsAny(A.add(n).asArray()), message + " added containsAny");

                // removed
                Assert.assertEquals(A.remove(n).asString(), new IntSpan(removed[i][j]).asString(), message + " removed");
                Assert.assertTrue(A.containsAll(A.remove(n).asArray()), message + " removed containsAll");
            }
        }

    }
}
