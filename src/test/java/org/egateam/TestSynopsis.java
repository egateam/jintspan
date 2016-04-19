/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package org.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestSynopsis {

    @Test
    public void prompt() {
        System.out.println("TestSynopsis");
    }

    private void testConstructors(IntSpan set) {
        System.out.println(set.asString()); // 1-3,5,7,9,100-999,1001-10000
        String expected = "1-3,5,7,9,100-999,1001-10000";
        Assert.assertEquals(set.asString(), expected);
        Assert.assertEquals(set.cardinality(), 9906);

        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.isNotEmpty());

        Assert.assertFalse(set.isInfinite());
        Assert.assertTrue(set.isFinite());
        Assert.assertFalse(set.isPosInf());
        Assert.assertFalse(set.isNegInf());
        Assert.assertFalse(set.isInfinite());
        Assert.assertFalse(set.isUniversal());
    }

    @Test
    public void testSynopsis() {

        // snippet 1
        {
            IntSpan set = new IntSpan();
            for ( int i : new int[]{1, 2, 3, 5, 7, 9} ) {
                set.add(i);
            }
            set.addPair(100, 10000);
            set.remove(1000);

            testConstructors(set);
        }

        {
            IntSpan set = new IntSpan();
            set.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 7, 9)));
            set.addPair(100, 10000);
            set.remove(1000);

            testConstructors(set);
        }

        {
            IntSpan set = new IntSpan(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 7, 9)));
            set.addPair(100, 10000);
            set.remove(1000);

            testConstructors(set);
        }

        {
            IntSpan set = new IntSpan();
            for ( int i : new int[]{1, 2, 3, 5, 7, 9} ) {
                set.add(i);
            }
            set.addPair(100, 10000);
            set.remove(1000);

            IntSpan set2 = new IntSpan(set.emptyString());
            set2.add(set);

            testConstructors(set2);
        }

        {
            IntSpan set = new IntSpan();

            IntSpan set1000 = new IntSpan(1000);
            ArrayList<Integer> ranges1000 = new ArrayList<Integer>(Arrays.asList(1000, 1000));

            set.addPair(1, 3).add(5).add(7).add(9).addPair(100, 10000)
                .merge(set1000).subtract(set1000)
                .remove(7).add(7)
                .remove("7").add("7")
                .add(1000).remove(1000)
                .addRange(ranges1000).removeRange(ranges1000)
                .addPair(1000, 1000).removePair(1000, 1000)
                .add(ranges1000).remove(ranges1000)
                .add("1000").remove("1000")
                .add(set1000).remove(set1000)
                .remove(1000);

            testConstructors(set);
        }

        // snippet 2
        {
            IntSpan infSet = new IntSpan().invert();

            System.out.println(infSet.asString());
            String expected = Integer.toString(infSet.negInf()) + "-" + Integer.toString(infSet.posInf());
            Assert.assertEquals(infSet.asString(), expected);

            Assert.assertFalse(infSet.isEmpty());
            Assert.assertTrue(infSet.isNotEmpty());

            Assert.assertTrue(infSet.isInfinite());
            Assert.assertFalse(infSet.isFinite());
            Assert.assertTrue(infSet.isPosInf());
            Assert.assertTrue(infSet.isNegInf());
            Assert.assertTrue(infSet.isInfinite());
            Assert.assertTrue(infSet.isUniversal());
        }

        // snippet 3
        {
            IntSpan posInfSet = new IntSpan();
            posInfSet.addPair(1, posInfSet.posInf());

            System.out.println(posInfSet.asString());
            String expected = "1-" + Integer.toString(posInfSet.posInf());
            Assert.assertEquals(posInfSet.asString(), expected);

            Assert.assertFalse(posInfSet.isEmpty());
            Assert.assertTrue(posInfSet.isNotEmpty());

            Assert.assertTrue(posInfSet.isInfinite());
            Assert.assertFalse(posInfSet.isFinite());
            Assert.assertTrue(posInfSet.isPosInf());
            Assert.assertFalse(posInfSet.isNegInf());
            Assert.assertTrue(posInfSet.isInfinite());
            Assert.assertFalse(posInfSet.isUniversal());
        }
    }
}
