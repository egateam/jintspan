/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package org.egateam;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSynopsis {

    @Test
    public void prompt() {
        System.out.println("TestSynopsis");
    }

    @Test
    public void testSynopsis() {

        { // snippet 1
            IntSpan set = new IntSpan();
            for ( int i : new int[]{1, 2, 3, 5, 7, 9} ) {
                set.add(i);
            }
            set.addPair(100, 10000);
            set.remove(1000);

            System.out.println(set.asString()); // 1-3,5,7,9,100-999,1001-10000
            String expected = "1-3,5,7,9,100-999,1001-10000";
            Assert.assertEquals(set.asString(), expected);

            Assert.assertFalse(set.isEmpty());
            Assert.assertTrue(set.isNotEmpty());

            Assert.assertFalse(set.isInfinite());
            Assert.assertTrue(set.isFinite());
            Assert.assertFalse(set.isPosInf());
            Assert.assertFalse(set.isNegInf());
            Assert.assertFalse(set.isInfinite());
            Assert.assertFalse(set.isUniversal());
        }

        { // snippet 2
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

        { // snippet 3
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
