/**
 * <tt>IntSpan</tt> handles of sets containing integer spans.
 * <p>
 * <strong>SYNOPSIS</strong>
 * <pre>
 *      import org.egateam.IntSpan;
 *
 *      IntSpan set = new IntSpan();
 *      for (int i : new int[]{1, 2, 3, 5, 7, 9} ) {
 *          set.add(i);
 *      }
 *      set.addPair(100, 10000);
 *      set.remove(1000);
 *      System.out.println(set.asString()); // 1-3,5,7,9,100-999,1001-10000
 * </pre>
 * <p>
 * <strong>DESCRIPTION</strong>
 * <p>
 * The class <tt>IntSpan</tt> represents sets of integers as a number of inclusive ranges, for
 * example '1-10,19-23,45-48'. Because many of its operations involve linear searches of the list of
 * ranges its overall performance tends to be proportional to the number of distinct ranges. This is
 * fine for small sets but suffers compared to other possible set representations (bit vectors, hash
 * keys) when the number of ranges grows large.
 * <p>
 * This module also represents sets as ranges of values but stores those ranges in order and uses a
 * binary search for many internal operations so that overall performance tends towards O log N
 * where N is the number of ranges.
 * <p>
 * The internal representation used by this module is extremely simple: a set is represented as a
 * list of integers. Integers in even numbered positions (0, 2, 4 etc) represent the start of a run
 * of numbers while those in odd numbered positions represent the ends of runs. As an example the
 * set (1, 3-7, 9, 11, 12) would be represented internally as (1, 2, 3, 8, 11, 13).
 * <p>
 * Sets may be infinite - assuming you're prepared to accept that infinity is actually no more than
 * a fairly large integer. Specifically the constants <tt>negINF</tt> and <tt>posINF</tt> are
 * defined to be (- 2^31 + 1) and (2^31 - 2) respectively. To create an infinite set invert an empty
 * one:
 * <pre>
 *     IntSpan infSet = new IntSpan().invert();
 * </pre>
 * <p>
 * Sets need only be bounded in one direction - for example this is the set of all positive integers
 * (assuming you accept the slightly feeble definition of infinity we're using):
 * <pre>
 *     IntSpan posInfSet = new IntSpan();
 *     posInfSet.addPair(1, posInfSet.posInf());
 * </pre>
 * <p>
 * This Java class is ported from the Perl module <tt>AlignDB::IntSpan</tt> which contains many
 * codes from <tt>Set::IntSpan</tt>, <tt>Set::IntSpan::Fast</tt> and <tt>Set::IntSpan::Island</tt>.
 * <p>
 * <strong>AUTHOR</strong>
 * Qiang Wang, wang-q@outlook.com
 * <p>
 * <strong>COPYRIGHT AND LICENSE</strong>
 * This software is copyright (c) 2016 by Qiang Wang.
 * <p>
 * This is free software; you can redistribute it and/or modify it under the same terms as the Perl
 * 5 programming language system itself.
 *
 * @author Qiang Wang
 * @since 1.6
 */

package org.egateam;

import java.util.ArrayList;

public class IntSpan {
    private static String emptyString = "-";

    // Real Largest int is posInf - 1
    private static int posInf = 2147483647 - 1; // INT_MAX - 1
    private static int negInf = -2147483648 + 1; // INT_MIN + 1

    //
    private ArrayList<Integer> edges = new ArrayList<Integer>();

    //----------------------------------------------------------
    // Constructors
    //----------------------------------------------------------
    public IntSpan() {
    }

    public IntSpan(int val) {
        addPair(val, val);
    }

    public IntSpan(IntSpan supplied) {
        edges = new ArrayList<Integer>(supplied.edges());
    }

    public IntSpan(String runlist) {
        add(runlist);
    }

    //----------------------------------------------------------
    // Set contents
    //----------------------------------------------------------
    public ArrayList<Integer> edges() {
        return edges;
    }

    public int posInf() {
        return posInf - 1;
    }

    public int negInf() {
        return negInf;
    }

    public int edgeSize() {
        return edges.size();
    }

    public int spanSize() {
        return edgeSize() / 2;
    }

    public String asString() {
        if ( isEmpty() ) {
            return emptyString;
        }

        String runlist = "";

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;

            String buf = "";
            if ( i != 0 ) {
                buf += ",";
            }

            if ( lower == upper ) {
                buf += Integer.toString(lower);
            } else {
                buf += Integer.toString(lower) + "-" + Integer.toString(upper);
            }

            runlist += buf;
        }

        return runlist;
    }

    public ArrayList<Integer> asArray() {
        ArrayList<Integer> array = new ArrayList<Integer>();
        if ( isEmpty() ) {
            return array;
        }

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;

            for ( int n = lower; n <= upper; n++ ) {
                array.add(n);
            }

        }

        return array;
    }

    public ArrayList<Integer> ranges() {
        ArrayList<Integer> ranges = new ArrayList<Integer>();
        if ( isEmpty() ) {
            return ranges;
        }

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;
            ranges.add(lower);
            ranges.add(upper);
        }
        return ranges;
    }

    //----------------------------------------------------------
    // Set cardinality
    //----------------------------------------------------------

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of elements in this set
     */
    public int cardinality() {
        int cardinality = 0;
        if ( isEmpty() ) {
            return cardinality;
        }

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;
            cardinality += upper - lower + 1;
        }

        return cardinality;
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements
     */
    public boolean isEmpty() {
        return edgeSize() == 0;
    }

    /**
     * Returns <tt>true</tt> if this set is not empty.
     *
     * @return <tt>true</tt> if this set is not empty
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set is negtive infinite.
     *
     * @return <tt>true</tt> if this set is negtive infinite
     */
    public boolean isNegInf() {
        return edges.get(0) == negInf;
    }

    /**
     * Returns <tt>true</tt> if this set is positive infinite.
     *
     * @return <tt>true</tt> if this set is positive infinite
     */
    public boolean isPosInf() {
        return edges.get(edges.size() - 1) == posInf;
    }

    /**
     * Returns <tt>true</tt> if this set is infinite.
     *
     * @return <tt>true</tt> if this set is infinite
     */
    public boolean isInfinite() {
        return isNegInf() || isPosInf();
    }

    /**
     * Returns <tt>true</tt> if this set is finite.
     *
     * @return <tt>true</tt> if this set is finite
     */
    public boolean isFinite() {
        return !isInfinite();
    }

    /**
     * Returns <tt>true</tt> if this set contains all integers.
     *
     * @return <tt>true</tt> if this set contains all integers
     */
    public boolean isUniversal() {
        return edgeSize() == 2 && isNegInf() && isPosInf();
    }

    //----------------------------------------------------------
    // Member operations (mutate original set)
    //----------------------------------------------------------
    public IntSpan addPair(int lower, int upper) {
        upper++;

        assert (lower <= upper) : "Bad order: " + Integer.toString(lower) + "," + Integer.toString(upper);

        int lowerPos = findPos(lower, 0);
        int upperPos = findPos(upper + 1, lowerPos);

        if ( (lowerPos & 1) == 1 ) {
            lower = edges.get(--lowerPos);
        }
        if ( (upperPos & 1) == 1 ) {
            upper = edges.get(upperPos++);
        }

        for ( int i = lowerPos; i < upperPos; i++ ) {
            edges.remove(lowerPos);
        }
        edges.add(lowerPos, lower);
        edges.add(lowerPos + 1, upper);

        return this;
    }

    public IntSpan addRange(ArrayList<Integer> ranges) {
        assert (ranges.size() % 2 == 0) : "Number of ranges must be even: @ranges\n";

        while ( ranges.size() > 0 ) {
            int lower = ranges.remove(0);
            int upper = ranges.remove(0);
            addPair(lower, upper);
        }

        return this;
    }

    public IntSpan merge(IntSpan supplied) {
        ArrayList<Integer> ranges = supplied.ranges();
        addRange(ranges);

        return this;
    }

    public IntSpan add(int n) {
        addPair(n, n);

        return this;
    }

    public IntSpan add(IntSpan supplied) {
        merge(supplied);

        return this;
    }

    public IntSpan add(String runlist) {
        runlist = stripWhitespace(runlist);

        // empty set
        if ( runlist.equals("") || runlist.equals(emptyString) ) {
            // Do nothing
        } else {
            addRange(runlistToRanges(runlist));
        }

        return this;
    }

    public IntSpan invert() {
        if ( isEmpty() ) {
            // Universal set
            edges = new ArrayList<Integer>();
            edges.add(negInf);
            edges.add(posInf);
        } else {
            // Either add or remove infinity from each end. The net effect is always an even number
            // of additions and deletions

            if ( isNegInf() ) {
                edges.remove(0); // shift
            } else {
                edges.add(0, negInf); // unshift
            }

            if ( isPosInf() ) {
                edges.remove(edges.size() - 1); // pop
            } else {
                edges.add(posInf); // push
            }
        }

        return this;
    }

    public IntSpan removePair(int lower, int upper) {
        invert();
        addPair(lower, upper);
        invert();

        return this;
    }

    public IntSpan removeRange(ArrayList<Integer> ranges) {
        assert (ranges.size() % 2 == 0) : "Number of ranges must be even: @ranges\n";

        invert();
        addRange(ranges);
        invert();

        return this;
    }

    public IntSpan subtract(IntSpan supplied) {
        ArrayList<Integer> ranges = supplied.ranges();
        removeRange(ranges);

        return this;
    }

    public IntSpan remove(int n) {
        removePair(n, n);

        return this;
    }

    public IntSpan remove(IntSpan supplied) {
        subtract(supplied);

        return this;
    }

    public IntSpan remove(String runlist) {
        runlist = stripWhitespace(runlist);

        // empty set
        if ( runlist.equals("") || runlist.equals(emptyString) ) {
            // Do nothing
        } else {
            removeRange(runlistToRanges(runlist));
        }

        return this;
    }

    //----------------------------------------------------------
    // Set binary operations ( create new set)
    //----------------------------------------------------------

    /**
     * Returns an identical copy of this <tt>IntSpan</tt> instance. The
     * elements themselves are also preserved.
     *
     * @return a copy of this <tt>IntSpan</tt> instance
     */
    public IntSpan copy() {
        IntSpan newSet = new IntSpan();

        newSet.edges = new ArrayList<Integer>(edges);

        return newSet;
    }

    /**
     * Returns a new set that is the union (并集) of this set and the supplied set.
     *
     * @param supplied set to be operated with this set
     * @return the union of this set and the supplied set
     */
    public IntSpan union(IntSpan supplied) {
        IntSpan newSet = copy();
        newSet.merge(supplied);

        return newSet;
    }

    /**
     * Returns a new set that is the absolute complement (绝对补集) of this set.
     *
     * @return the absolute complement of this set
     */
    public IntSpan complement() {
        IntSpan newSet = copy();
        newSet.invert();

        return newSet;
    }

    /**
     * Returns a new set that is the relative complement (相对补集) of the supplied set in this set.
     * Also termed the set-theoretic difference of this set and the supplied set.
     * <p>
     * In other words, the set of elements in this set, but not in the supplied set.
     *
     * @param supplied set to be operated with this set
     * @return the relative complement of the supplied set in this set
     */
    public IntSpan diff(IntSpan supplied) {
        if ( isEmpty() ) {
            return this;
        } else {
            IntSpan newSet = copy();
            newSet.subtract(supplied);

            return newSet;
        }
    }

    /**
     * Returns a new set that is the intersection (交集) of this set and the supplied set.
     *
     * @param supplied set to be operated with this set
     * @return the intersection of this set and the supplied set
     */
    public IntSpan intersect(IntSpan supplied) {
        if ( isEmpty() ) {
            return this;
        } else {
            IntSpan newSet = complement();
            newSet.merge(supplied.complement());
            newSet.invert();

            return newSet;
        }
    }

    /**
     * Return a new set that contains all of the members that are in this set or the
     * supplied set but not both.
     *
     * @param supplied set to be operated with this set
     * @return a new set that contains all of the members that are in this set or the supplied set
     * but not both
     */
    public IntSpan xor(IntSpan supplied) {
        IntSpan newSet = union(supplied);
        newSet.subtract(intersect(supplied));

        return newSet;
    }

    //----------------------------------------------------------
    // Set relations
    //----------------------------------------------------------
    public boolean equals(IntSpan supplied) {
        ArrayList<Integer> edges_a = this.edges();
        ArrayList<Integer> edges_b = supplied.edges();

        if ( edges_a.size() != edges_b.size() ) {
            return false;
        }

        for ( int i = 0; i < edges_a.size(); i++ ) {
            int a = edges_a.get(i);
            int b = edges_b.get(i);
            if ( a != b ) {
                return false;
            }
        }

        return true;
    }

    public boolean subset(IntSpan supplied) {
        return this.diff(supplied).isEmpty();
    }

    public boolean superset(IntSpan supplied) {
        return supplied.diff(this).isEmpty();
    }

    //----------------------------------------------------------
    // Indexing
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Extrema
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Spans operations
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Islands
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Private methods
    //----------------------------------------------------------

    private static String stripWhitespace(String string) {
        String result = "";

        String[] str = string.split("\\s");
        for ( String s : str ) {
            result += s;
        }

        return result;
    }

    private ArrayList<Integer> runlistToRanges(String runlist) {
        ArrayList<Integer> ranges = new ArrayList<Integer>();

        String[] str = runlist.split(",");
        for ( String s : str ) {
            boolean lowerIsNeg = s.substring(0, 1).equals("-");
            boolean upperIsNeg = s.contains("--");

            String[] str2 = s.split("-");
            ArrayList<Integer> range = new ArrayList<Integer>();
            for ( String s2 : str2 ) {
                if ( !s2.equals("") ) {
                    range.add(Integer.parseInt(s2));
                }
            }

            int lower = 1, upper = -1; // variables need be initialized

            if ( range.size() == 1 ) {
                lower = range.get(0);
                if ( lowerIsNeg ) {
                    lower = -lower;
                }
                upper = lower;
            } else if ( range.size() == 2 ) {
                lower = range.get(0);
                if ( lowerIsNeg ) {
                    lower = -lower;
                }

                upper = range.get(1);
                if ( upperIsNeg ) {
                    upper = -upper;
                }
            } else {
                assert false : "Single run errors [" + s + "]. Size of tokens is " + range.size();
            }

            assert (lower <= upper) : "Bad order [" + s + "]";
            ranges.add(lower);
            ranges.add(upper);
        }

        return ranges;
    }

    /*
    Return the index of the first element >= the supplied value.

    If the supplied value is larger than any element in the list the returned
    value will be equal to the size of the list.

    If (pos & 1) == 1, i.e. pos is odd number, val is in the set
    */
    private int findPos(int val, int low) {
        int high = edgeSize();

        while ( low < high ) {
            int mid = (low + high) / 2;
            if ( val < edges.get(mid) ) {
                high = mid;
            } else if ( val > edges.get(mid) ) {
                low = mid + 1;
            } else {
                return mid;
            }
        }

        return low;
    }

    //----------------------------------------------------------
    // Aliases
    //----------------------------------------------------------
    public int size() {
        return cardinality();
    }

    public int count() {
        return cardinality();
    }

    public String runlist() {
        return asString();
    }

    public ArrayList<Integer> elements() {
        return asArray();
    }

    public boolean equal(IntSpan supplied) {
        return equals(supplied);
    }

}
