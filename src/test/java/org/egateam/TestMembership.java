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
        boolean containsAll = true, containsAny = true;
        for ( int i = 0; i < sets.length; i++ ) {
            for ( int j = 0; j < sets.length; j++ ) {
                String message = "Test " + i + " " + j;

                int n = j + 1;

                IntSpan set = new IntSpan(sets[i]);
                IntSpan setAdded = set.copy().add(n);
                IntSpan setRemoved = set.copy().remove(n);

                // contains
                Assert.assertEquals(set.contains(n), contains[i][j] != 0, message + " contains");

                // added
                Assert.assertEquals(setAdded.asString(), new IntSpan(added[i][j]).asString(), message + " added");
                if ( set.isNotEmpty() ) {
                    Assert.assertTrue(set.containsAny(setAdded.asArray()), message + " added containsAny");
                }
                containsAll = containsAll && set.containsAll(setAdded.asArray()); // shouldn't all be true

                // removed
                Assert.assertEquals(setRemoved.asString(), new IntSpan(removed[i][j]).asString(), message + " removed");
                Assert.assertTrue(set.containsAll(setRemoved.asArray()), message + " removed containsAll");
                if ( set.isNotEmpty() && setRemoved.isNotEmpty() ) {
                    containsAny = containsAny && set.containsAny(setRemoved.asArray()); // should all be true
                }
            }
        }

        Assert.assertFalse(containsAll);
        Assert.assertTrue(containsAny);
    }
}
