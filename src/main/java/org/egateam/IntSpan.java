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
 *
 *      // or use ArrayList, that's faster but more trivial
 *      // set.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 7, 9)));
 *      // or
 *      // IntSpan set = new IntSpan(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 7, 9)));
 *
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
 *     posInfSet.addPair(1, IntSpan.getPosInf());
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
import java.util.Collections;

@SuppressWarnings("WeakerAccess")
public class IntSpan {
    private static final String emptyString = "-";

    // Real Largest int is getPosInf - 1
    private static final int posInf = 2147483647 - 1; // INT_MAX - 1
    private static final int negInf = -2147483648 + 1; // INT_MIN + 1

    private ArrayList<Integer> edges = new ArrayList<Integer>();

    //----------------------------------------------------------
    // Constructors
    //----------------------------------------------------------

    /**
     * Constructs an empty set.
     */
    public IntSpan() {
    }

    /**
     * Constructs a set with a single elements.
     */
    public IntSpan(int val) {
        addPair(val, val);
    }

    /**
     * Constructs a set with a pair of integers constituting a range.
     *
     * @param lower lower boundary
     * @param upper upper boundary ( upper must >= lower)
     */
    public IntSpan(int lower, int upper) {
        addPair(lower, upper);
    }

    /**
     * Constructs a set with all elements in ArrayList.
     */
    public IntSpan(ArrayList<Integer> list) {
        ArrayList<Integer> ranges = listToRanges(list);
        addRange(ranges);
    }

    /**
     * Constructs a copy set of the supplied set.
     */
    public IntSpan(IntSpan supplied) {
        edges = new ArrayList<Integer>(supplied.edges());
    }

    /**
     * Constructs a set from the runlist string.
     */
    public IntSpan(String runlist) {
        add(runlist);
    }

    //----------------------------------------------------------
    // Constants
    //----------------------------------------------------------

    /**
     * Normally used in construction of infinite sets.
     *
     * @return positive infinity
     */
    public static int getPosInf() {
        return posInf - 1;
    }

    /**
     * Normally used in construction of infinite sets.
     *
     * @return negative infinity
     */
    public static int getNegInf() {
        return negInf;
    }

    /**
     * Useless in common cases.
     *
     * @return empty string "-"
     */
    public static String getEmptyString() {
        return emptyString;
    }

    //----------------------------------------------------------
    // Set contents
    //----------------------------------------------------------

    /**
     * Clear all elements of this set.
     *
     * @return this set for method chaining
     */
    public IntSpan clear() {
        edges = new ArrayList<Integer>();

        return this;
    }

    /**
     * Returns the internal used ArrayList representing the set.
     * <p>
     * I don't think you should use this method.
     *
     * @return the internal used ArrayList representing this set
     */
    private ArrayList<Integer> edges() {
        return edges;
    }

    /**
     * Returns the number of edges.
     *
     * @return the number of edges
     */
    public int edgeSize() {
        return edges.size();
    }

    /**
     * Returns the number of spans.
     *
     * @return the number of spans
     */
    public int spanSize() {
        return edgeSize() / 2;
    }

    /**
     * Returns a string representation of this set.
     *
     * @return a string representation of this set
     */
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

    /**
     * Returns an ArrayList containing all elements of this set in ascending order.
     *
     * @return an ArrayList containing all elements of this set in ascending order
     */
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

    /**
     * Returns the runs in this set, as a list of (lower, upper)
     *
     * @return the runs in this set, as a list of (lower, upper)
     */
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
     * Returns <tt>true</tt> if this set is negative infinite.
     *
     * @return <tt>true</tt> if this set is negative infinite
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
    // Membership test
    //----------------------------------------------------------

    /**
     * Returns <tt>true</tt> if this set contains all of the specified numbers.
     *
     * @param list the specified numbers
     * @return <tt>true</tt> if this set contains all of the specified numbers
     */
    public boolean containsAll(ArrayList<Integer> list) {
        for ( int i : list ) {
            int pos = findPos(i + 1, 0);
            if ( (pos & 1) != 1 ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified number.
     *
     * @param n the specified number
     * @return <tt>true</tt> if this set contains the specified number
     */
    public boolean contains(int n) {
        int pos = findPos(n + 1, 0);
        return (pos & 1) == 1;
    }

    /**
     * Returns <tt>true</tt> if this set contains any of the specified numbers.
     *
     * @param list the specified numbers
     * @return <tt>true</tt> if this set contains any of the specified numbers
     */
    public boolean containsAny(ArrayList<Integer> list) {
        for ( int i : list ) {
            int pos = findPos(i + 1, 0);
            if ( (pos & 1) == 1 ) {
                return true;
            }
        }

        return false;
    }

    //----------------------------------------------------------
    // Member operations (mutate original set)
    //----------------------------------------------------------

    /**
     * Adds a pair of inclusive integers to this set.
     * <p>
     * A pair of integers constitute a range.
     *
     * @param lower lower boundary
     * @param upper upper boundary ( upper must >= lower)
     * @return this set for method chaining
     */
    public IntSpan addPair(int lower, int upper) throws AssertionError {
        upper++;

        if ( lower > upper )
            throw new AssertionError(String.format("Bad order: %s,%s", Integer.toString(lower), Integer.toString(upper)));

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

    /**
     * Adds the inclusive range of integers to this set.
     * <p>
     * Multiple ranges may be specified. Each pair of integers constitute a range.
     *
     * @param ranges the inclusive ranges of integers (ranges.size() must be even)
     * @return this set for method chaining
     */
    public IntSpan addRange(ArrayList<Integer> ranges) throws AssertionError {
        if ( ranges.size() % 2 != 0 ) throw new AssertionError("Number of ranges must be even");

        while ( ranges.size() > 0 ) {
            int lower = ranges.remove(0);
            int upper = ranges.remove(0);
            addPair(lower, upper);
        }

        return this;
    }

    /**
     * Merges the members of the supplied set into this set.
     *
     * @param supplied the supplied set
     * @return this set for method chaining
     */
    public IntSpan merge(IntSpan supplied) {
        ArrayList<Integer> ranges = supplied.ranges();
        addRange(ranges);

        return this;
    }

    public IntSpan add(int n) {
        addPair(n, n);

        return this;
    }

    public IntSpan add(ArrayList<Integer> list) {
        ArrayList<Integer> ranges = listToRanges(list);
        addRange(ranges);

        return this;
    }

    public IntSpan add(IntSpan supplied) {
        merge(supplied);

        return this;
    }

    public IntSpan add(String runlist) {
        runlist = stripWhitespace(runlist);

        // skip empty set
        if ( !runlist.equals("") && !runlist.equals(emptyString) ) {
            addRange(runlistToRanges(runlist));
        }

        return this;
    }

    /**
     * Complement this set.
     * <p>
     * Because our notion of infinity is actually disappointingly finite inverting a finite set
     * results in another finite set. For example inverting the empty set makes it contain all the
     * integers between negInf and posInf inclusive.
     * <p>
     * As noted above negInf and posInf are actually just big integers.
     *
     * @return this set for method chaining
     */
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

    /**
     * Removes a pair of inclusive integers from this set.
     *
     * @param lower lower boundary
     * @param upper upper boundary ( upper must >= lower)
     * @return this set for method chaining
     */
    public IntSpan removePair(int lower, int upper) {
        invert();
        addPair(lower, upper);
        invert();

        return this;
    }

    /**
     * Removes the inclusive range of integers from this set.
     * <p>
     * Multiple ranges may be specified. Each pair of integers constitute a range.
     *
     * @param ranges the inclusive ranges of integers (ranges.size() must be even)
     * @return this set for method chaining
     */
    public IntSpan removeRange(ArrayList<Integer> ranges) throws AssertionError {
        if ( ranges.size() % 2 != 0 ) throw new AssertionError("Number of ranges must be even");

        invert();
        addRange(ranges);
        invert();

        return this;
    }

    /**
     * Subtracts the members of the supplied set out of this set.
     *
     * @param supplied the supplied set
     * @return this set for method chaining
     */
    public IntSpan subtract(IntSpan supplied) {
        ArrayList<Integer> ranges = supplied.ranges();
        removeRange(ranges);

        return this;
    }

    public IntSpan remove(int n) {
        removePair(n, n);

        return this;
    }

    public IntSpan remove(ArrayList<Integer> list) {
        ArrayList<Integer> ranges = listToRanges(list);
        removeRange(ranges);

        return this;
    }

    public IntSpan remove(IntSpan supplied) {
        subtract(supplied);

        return this;
    }

    public IntSpan remove(String runlist) {
        runlist = stripWhitespace(runlist);

        // empty set
        if ( !runlist.equals("") && !runlist.equals(emptyString) ) {
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

    /**
     * Returns <tt>true</tt> if this set and the supplied set contain the same elements.
     *
     * @return <tt>true</tt> if this set and the supplied set contain the same elements
     */
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

    /**
     * Returns <tt>true</tt> if this set is a subset of the supplied set.
     *
     * @return <tt>true</tt> if this set is a subset of the supplied set
     */
    public boolean subset(IntSpan supplied) {
        return this.diff(supplied).isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set is a superset of the supplied set.
     *
     * @return <tt>true</tt> if this set is a superset of the supplied set
     */
    public boolean superset(IntSpan supplied) {
        return supplied.diff(this).isEmpty();
    }

    //----------------------------------------------------------
    // Indexing
    //----------------------------------------------------------

    /*sub at {
    my $self  = shift;
    my $index = shift;
    if ( $index == 0 || abs($index) > $self->cardinality ) {
        return;
    }
    my $member = $index < 0 ? $self->_at_neg( -$index ) : $self->_at_pos($index);
    return $member;
}
*/

    /**
     * Returns the (index)th element of set, index start from "1".
     * <p>
     * Negative indices count backwards from the end of the set.
     * <p>
     * Index can't be "0".
     *
     * @param index index in this set
     * @return the (index)th element of set
     * @throws AssertionError
     */
    public int at(int index) throws AssertionError {
        if ( !isNotEmpty() ) throw new AssertionError("Can't get indexing on an empty set");
        if ( Math.abs(index) < 1 ) throw new AssertionError("Index start from 1");
        if ( Math.abs(index) > cardinality() ) throw new AssertionError("Out of max index");

        if ( index > 0 ) {
            return atPos(index);
        } else {
            return atNeg(-index);
        }
    }

    //----------------------------------------------------------
    // Extrema
    //----------------------------------------------------------

    /**
     * Returns the smallest element of this set (can't be empty).
     *
     * @return the smallest element of this set
     * @throws AssertionError
     */
    public int min() throws AssertionError {
        if ( !isNotEmpty() ) throw new AssertionError();
        return edges.get(0);
    }

    /**
     * Returns the largest element of this set (can't be empty).
     *
     * @return the largest element of this set
     * @throws AssertionError
     */
    public int max() throws AssertionError {
        if ( !isNotEmpty() ) throw new AssertionError();
        return edges.get(edges.size() - 1);
    }

    //----------------------------------------------------------
    // Spans operations
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Islands
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Private methods
    //----------------------------------------------------------

    private ArrayList<Integer> listToRanges(ArrayList<Integer> list) {
        Collections.sort(list);

        ArrayList<Integer> ranges = new ArrayList<Integer>();
        int count = list.size();
        int pos = 0;

        while ( pos < count ) {
            int end = pos + 1;
            while ( (end < count) && (list.get(end) <= list.get(end - 1) + 1) ) {
                end++;
            }
            ranges.add(list.get(pos));
            ranges.add(list.get(end - 1));
            pos = end;
        }

        return ranges;
    }

    private static String stripWhitespace(String string) {
        String result = "";

        String[] str = string.split("\\s");
        for ( String s : str ) {
            result += s;
        }

        return result;
    }

    private ArrayList<Integer> runlistToRanges(String runlist) throws AssertionError {
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

            int lower, upper;

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
                throw new AssertionError(String.format("Single run errors [%s]. Size of tokens is %d", s, range.size()));
            }

            if ( lower > upper ) throw new AssertionError(String.format("Bad order [%s]", s));
            ranges.add(lower);
            ranges.add(upper);
        }

        return ranges;
    }

    /**
     * Return the index of the first element >= the supplied value.
     * <p>
     * If the supplied value is larger than any element in the list the returned value will be equal
     * to the size of the list.
     * <p>
     * If (pos & 1) == 1, i.e. pos is odd number, val is in the set
     *
     * @param val supplied value
     * @param low start value
     * @return the index of the first element >= the supplied value.
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

    private int atPos(int index) {
        int member = min();
        int countOfElementsBefore = 0;

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( index > countOfElementsBefore + thisSpanSize ) {
                countOfElementsBefore += thisSpanSize;
            } else {
                member = index - countOfElementsBefore - 1 + lower;
                break;
            }
        }

        return member;
    }

    private int atNeg(int index) {
        int member = max();
        int countOfElementsAfter = 0;

        for ( int i = spanSize() - 1; i >= 0; i-- ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( index > countOfElementsAfter + thisSpanSize ) {
                countOfElementsAfter += thisSpanSize;
            } else {
                member = upper - (index - countOfElementsAfter) + 1;
                break;
            }
        }

        return member;
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

    public IntSpan intersection(IntSpan supplied) {
        return intersect(supplied);
    }
}
