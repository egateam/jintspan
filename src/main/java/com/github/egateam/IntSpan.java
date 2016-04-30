/**
 * <tt>IntSpan</tt> handles of sets containing integer spans.
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
 * @since 1.7
 */

package com.github.egateam;

import com.carrotsearch.hppc.IntArrayList;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class IntSpan {
    private static final String EMPTY_STRING = "-";

    // Real Largest int is POS_INF - 1
    private static final int POS_INF = 2147483647 - 1; // INT_MAX - 1
    private static final int NEG_INF = -2147483648 + 1; // INT_MIN + 1

    // HPPC IntArrayList (less memory than ArrayList<Integer>)
    private IntArrayList edges = new IntArrayList();

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
     *
     * @param val a valid integer
     */
    public IntSpan(int val) {
        addPair(val, val);
    }

    /**
     * Constructs a set with a pair of integers constituting a range.
     *
     * @param lower lower boundary
     * @param upper upper boundary ( upper must be larger than or equals to lower)
     */
    public IntSpan(int lower, int upper) {
        addPair(lower, upper);
    }

    /**
     * Constructs a set with all elements in Array.
     *
     * @param ints integer array to add to this set
     */
    public IntSpan(int[] ints) {
        add(ints);
    }

    /**
     * Constructs a copy set of the supplied set.
     *
     * @param supplied the supplied set
     */
    public IntSpan(IntSpan supplied) {
        edges = new IntArrayList(supplied.getEdges());
    }

    /**
     * Constructs a set from the runlist string.
     *
     * @param runlist IntSpan string presentation
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
        return POS_INF - 1;
    }

    /**
     * Normally used in construction of infinite sets.
     *
     * @return negative infinity
     */
    public static int getNegInf() {
        return NEG_INF;
    }

    /**
     * Useless in common cases.
     *
     * @return empty string "-"
     */
    public static String getEmptyString() {
        return EMPTY_STRING;
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
        edges = new IntArrayList();

        return this;
    }

    /**
     * Returns the internal used ArrayList representing the set.
     * <p>
     * I don't think you should use this method.
     *
     * @return the internal used ArrayList representing this set
     */
    private IntArrayList getEdges() {
        return edges;
    }

    /**
     * Returns the number of getEdges.
     *
     * @return the number of getEdges
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
    @Override
    public String toString() {
        if ( isEmpty() ) {
            return EMPTY_STRING;
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
     * Returns an int[] containing all elements of this set in ascending order.
     *
     * @return an int[] containing all elements of this set in ascending order
     */
    public int[] toArray() {
        IntArrayList list = new IntArrayList();
        if ( isEmpty() ) {
            return list.toArray();
        }

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;

            int   spanSize     = upper - lower + 1;
            int[] spanElements = new int[spanSize];
            for ( int j = 0; j < spanSize; j++ ) {
                spanElements[j] = lower + j;
            }

            list.add(spanElements);
        }

        return list.toArray();
    }

    /**
     * Returns the runs in this set, as a list of (lower, upper)
     *
     * @return the runs in this set, as a list of (lower, upper)
     */
    public IntArrayList ranges() {
        IntArrayList ranges = edges.clone();

        for ( int i = 0; i < ranges.size(); i++ ) {
            // odd index means upper
            if ( (i & 1) == 1 ) {
                ranges.set(i, ranges.get(i) - 1);
            }
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
        return edges.get(0) == NEG_INF;
    }

    /**
     * Returns <tt>true</tt> if this set is positive infinite.
     *
     * @return <tt>true</tt> if this set is positive infinite
     */
    public boolean isPosInf() {
        return edges.get(edges.size() - 1) == POS_INF;
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
     * @param ints the specified numbers
     * @return <tt>true</tt> if this set contains all of the specified numbers
     */
    public boolean containsAll(int[] ints) {
        for ( int i : ints ) {
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
     * @param ints the specified numbers
     * @return <tt>true</tt> if this set contains any of the specified numbers
     */
    public boolean containsAny(int[] ints) {
        for ( int i : ints ) {
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
     * @param upper upper boundary ( upper must be larger than or equals to lower)
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

        edges.removeRange(lowerPos, upperPos);
        edges.insert(lowerPos, lower);
        edges.insert(lowerPos + 1, upper);

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
    public IntSpan addRange(IntArrayList ranges) throws AssertionError {
        if ( ranges.size() % 2 != 0 ) throw new AssertionError("Number of ranges must be even");

        // When this IntSpan is empty, just convert ranges to edges
        if ( isEmpty() ) {
            edges = ranges.clone();

            for ( int i = 0; i < edges.size(); i++ ) {
                // odd index means upper
                if ( (i & 1) == 1 ) {
                    edges.set(i, edges.get(i) + 1);
                }
            }
        } else {
            edges.ensureCapacity(ranges.size());
            for ( int i = 0; i < ranges.size() / 2; i++ ) {
                int lower = ranges.get(i * 2);
                int upper = ranges.get(i * 2 + 1);

                addPair(lower, upper);
            }
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
        IntArrayList ranges = supplied.ranges();
        addRange(ranges);

        return this;
    }

    public IntSpan add(int n) {
        addPair(n, n);

        return this;
    }

    public IntSpan add(int[] array) {
        IntArrayList ranges = listToRanges(array);
        addRange(ranges);

        return this;
    }

    public IntSpan add(IntSpan supplied) {
        merge(supplied);

        return this;
    }

    public IntSpan add(String runlist) {
        // skip empty set
        if ( !runlist.isEmpty() && !runlist.equals(EMPTY_STRING) ) {
            addRange(runlistToRanges(runlist));
        }

        return this;
    }

    /**
     * Complement this set.
     * <p>
     * Because our notion of infinity is actually disappointingly finite inverting a finite set
     * results in another finite set. For example inverting the empty set makes it contain all the
     * integers between NEG_INF and POS_INF inclusive.
     * <p>
     * As noted above NEG_INF and POS_INF are actually just big integers.
     *
     * @return this set for method chaining
     */
    public IntSpan invert() {
        if ( isEmpty() ) {
            // Universal set
            edges = new IntArrayList();
            edges.add(NEG_INF, POS_INF);
        } else {
            // Either add or remove infinity from each end. The net effect is always an even number
            // of additions and deletions

            if ( isNegInf() ) {
                edges.remove(0); // shift
            } else {
                edges.insert(0, NEG_INF); // unshift
            }

            if ( isPosInf() ) {
                edges.remove(edges.size() - 1); // pop
            } else {
                edges.add(POS_INF); // push
            }
        }

        return this;
    }

    /**
     * Removes a pair of inclusive integers from this set.
     *
     * @param lower lower boundary
     * @param upper upper boundary ( upper must be larger than or equals to lower)
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
    public IntSpan removeRange(IntArrayList ranges) throws AssertionError {
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
        IntArrayList ranges = supplied.ranges();
        removeRange(ranges);

        return this;
    }

    public IntSpan remove(int n) {
        removePair(n, n);

        return this;
    }

    public IntSpan remove(int[] ints) {
        IntArrayList ranges = listToRanges(ints);
        removeRange(ranges);

        return this;
    }

    public IntSpan remove(IntSpan supplied) {
        subtract(supplied);

        return this;
    }

    public IntSpan remove(String runlist) {
        // empty set
        if ( !runlist.isEmpty() && !runlist.equals(EMPTY_STRING) ) {
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

        newSet.edges = edges.clone();

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
            return new IntSpan();
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
        if ( this.isEmpty() || supplied.isEmpty() ) {
            return new IntSpan();
        }

        // when supplied IntSpan larger than this, swap
        // no good effects
//        if ( this.edgeSize() > supplied.edgeSize() ) {
//            IntSpan newSet = complement();
//            newSet.merge(supplied.complement());
//            newSet.invert();
//
//            return newSet;
//        } else {
//            IntSpan newSet = supplied.complement();
//            newSet.merge(this.complement());
//            newSet.invert();
//
//            return newSet;
//        }

        IntSpan newSet = complement();
        newSet.merge(supplied.complement());
        newSet.invert();

        return newSet;
    }

    /**
     * Return a new set that contains all of the members that are in this set or the
     * supplied set but not both.
     *
     * @param supplied set to be operated
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
     * @param supplied set to be compared
     * @return <tt>true</tt> if this set and the supplied set contain the same elements
     */
    public boolean equals(IntSpan supplied) {
        IntArrayList edges_a = this.getEdges();
        IntArrayList edges_b = supplied.getEdges();

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
     * @param supplied set to be compared
     * @return <tt>true</tt> if this set is a subset of the supplied set
     */
    public boolean subset(IntSpan supplied) {
        return this.diff(supplied).isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set is a superset of the supplied set.
     *
     * @param supplied set to be compared
     * @return <tt>true</tt> if this set is a superset of the supplied set
     */
    public boolean superset(IntSpan supplied) {
        return supplied.diff(this).isEmpty();
    }

    //----------------------------------------------------------
    // Extrema
    //----------------------------------------------------------

    /**
     * Returns the smallest element of this set (can't be empty).
     *
     * @return the smallest element of this set
     * @throws AssertionError for empty IntSpan
     */
    public int min() throws AssertionError {
        if ( !isNotEmpty() ) throw new AssertionError("Can't get extrema for empty IntSpan");
        return edges.get(0);
    }

    /**
     * Returns the largest element of this set (can't be empty).
     *
     * @return the largest element of this set
     * @throws AssertionError for empty IntSpan
     */
    public int max() throws AssertionError {
        if ( !isNotEmpty() ) throw new AssertionError("Can't get extrema for empty IntSpan");
        return edges.get(edges.size() - 1) - 1;
    }

    //----------------------------------------------------------
    // Indexing
    //----------------------------------------------------------

    /**
     * Returns the (index)th element of set, index start from "1".
     * <p>
     * Negative indices count backwards from the end of the set.
     * <p>
     * Index can't be "0".
     *
     * @param index index in this set
     * @return the (index)th element of set
     * @throws AssertionError for empty IntSpan and invalid index
     */
    public int at(int index) throws AssertionError {
        if ( isEmpty() ) throw new AssertionError("Indexing on an empty set");
        if ( Math.abs(index) < 1 ) throw new AssertionError("Index start from 1");
        if ( Math.abs(index) > cardinality() ) throw new AssertionError("Out of max index");

        if ( index > 0 ) {
            return atPos(index);
        } else {
            return atNeg(-index);
        }
    }

    private int atPos(int index) {
        int element               = min();
        int countOfElementsBefore = 0;

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower        = edges.get(i * 2);
            int upper        = edges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( index > countOfElementsBefore + thisSpanSize ) {
                countOfElementsBefore += thisSpanSize;
            } else {
                element = index - countOfElementsBefore - 1 + lower;
                break;
            }
        }

        return element;
    }

    private int atNeg(int index) {
        int element              = max();
        int countOfElementsAfter = 0;

        for ( int i = spanSize() - 1; i >= 0; i-- ) {
            int lower        = edges.get(i * 2);
            int upper        = edges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( index > countOfElementsAfter + thisSpanSize ) {
                countOfElementsAfter += thisSpanSize;
            } else {
                element = upper - (index - countOfElementsAfter) + 1;
                break;
            }
        }

        return element;
    }

    /**
     * Returns the index of an element in this set, index start from "1"
     *
     * @param element the element
     * @return the index of an element in this set
     * @throws AssertionError for empty IntSpan and invalid index
     */
    public int index(int element) throws AssertionError {
        if ( isEmpty() ) throw new AssertionError("Indexing on an empty set");
        if ( !contains(element) ) throw new AssertionError("Element doesn't exist");

        int index                 = -1; // not valid
        int countOfElementsBefore = 0;

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower        = edges.get(i * 2);
            int upper        = edges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( element >= lower && element <= upper ) {
                index = element - lower + 1 + countOfElementsBefore;
            } else {
                countOfElementsBefore += thisSpanSize;
            }
        }

        return index;
    }

    // TODO: slice()

    //----------------------------------------------------------
    // Spans operations
    //----------------------------------------------------------

    /**
     * Returns a set consisting of a single span from set.min() to set.max().
     *
     * @return a set consisting of a single span from set.min() to set.max()
     */
    public IntSpan cover() {
        IntSpan newSet = new IntSpan();
        if ( isNotEmpty() ) {
            newSet.addPair(min(), max());
        }
        return newSet;
    }

    /**
     * Returns a set containing all the holes in this set, that is, all the integers that are in-between
     * spans of this set.
     *
     * @return a set containing all the holes in this set
     */
    public IntSpan holes() {
        IntSpan newSet = new IntSpan();

        if ( isEmpty() || isUniversal() ) { // empty and universal set have no holes
            return newSet;
        } else {
            IntSpan      complementSet = complement();
            IntArrayList ranges        = complementSet.ranges();

            // Remove infinite arms of complement set
            if ( complementSet.isNegInf() ) {
                ranges.remove(0);
                ranges.remove(0);
            }
            if ( complementSet.isPosInf() ) {
                ranges.remove(ranges.size() - 1);
                ranges.remove(ranges.size() - 1);
            }
            newSet.addRange(ranges);

            return newSet;
        }
    }

    /**
     * Returns a set constructed by removing n integers from each end of each span of this set. If
     * n is negative, then -n integers are added to each end of each span.
     * <p>
     * In the first case, spans may vanish from this set; in the second case, holes may vanish.
     *
     * @param n integer
     * @return a set constructed by removing n integers from each end of each span of this set
     */
    public IntSpan inset(int n) {
        IntSpan newSet = new IntSpan();

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower = edges.get(i * 2);
            int upper = edges.get(i * 2 + 1) - 1;

            if ( lower != getNegInf() ) {
                lower += n;
            }
            if ( upper != getPosInf() ) {
                upper -= n;
            }

            if ( lower <= upper ) {
                newSet.addPair(lower, upper);
            }
        }

        return newSet;
    }

    /**
     * trim is provided as a synonym for inset.
     *
     * @param n integer
     * @return a set
     */
    public IntSpan trim(int n) {
        return inset(n);
    }

    /**
     * set.pad(n) is the same as set.inset(-n).
     *
     * @param n integer
     * @return a set
     */
    public IntSpan pad(int n) {
        return inset(-n);
    }

    /**
     * Removes all spans within this <strong>smaller than</strong> minLength
     *
     * @param minLength integer
     * @return a new set
     */
    public IntSpan excise(int minLength) {
        IntSpan newSet = new IntSpan();

        for ( int i = 0; i < spanSize(); i++ ) {
            int lower        = edges.get(i * 2);
            int upper        = edges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( thisSpanSize >= minLength ) {
                newSet.addPair(lower, upper);
            }
        }

        return newSet;
    }

    /**
     * Fills in all holes in this set <strong>smaller than or equals to </strong> maxLength
     *
     * @param maxLength integer
     * @return a new set
     */
    public IntSpan fill(int maxLength) {
        IntSpan newSet = copy();

        IntSpan      holesSet   = holes();
        IntArrayList holesEdges = holesSet.getEdges();

        for ( int i = 0; i < holesSet.spanSize(); i++ ) {
            int lower        = holesEdges.get(i * 2);
            int upper        = holesEdges.get(i * 2 + 1) - 1;
            int thisSpanSize = upper - lower + 1;

            if ( thisSpanSize <= maxLength ) {
                newSet.addPair(lower, upper);
            }
        }

        return newSet;
    }

    //----------------------------------------------------------
    // TODO: Inter-set operations
    //----------------------------------------------------------

    //----------------------------------------------------------
    // TODO: Islands
    //----------------------------------------------------------

    //----------------------------------------------------------
    // Private methods
    //----------------------------------------------------------

    private static IntArrayList listToRanges(int[] ints) {
        Arrays.sort(ints);

        IntArrayList ranges = new IntArrayList();

        int len = ints.length;
        int pos = 0;

        while ( pos < ints.length ) {
            int end = pos + 1;
            while ( (end < len) && (ints[end] <= ints[end - 1] + 1) ) {
                end++;
            }
            ranges.add(ints[pos], ints[end - 1]);
            pos = end;
        }

        return ranges;
    }

    private static IntArrayList runlistToRanges(String s) {
        IntArrayList ranges = new IntArrayList();

        int radix = 10;
        int idx   = 0; // index in runlist
        int len   = s.length();

        boolean lowerNeg = false;
        boolean upperNeg = false;
        boolean inUpper  = false;

        while ( idx < len ) {
            int i = 0; // index in one run
            if ( s.charAt(idx) == '-' ) {
                lowerNeg = true;
                i++;
            }

            // Integer.parseInt() say this:
            // Accumulating negatively avoids surprises near MAX_VALUE
            int lower = 0, upper = 0;
            for ( ; idx + i < len; i++ ) {
                char ch = s.charAt(idx + i);
                if ( ch >= '0' && ch <= '9' ) {
                    if ( !inUpper ) {

                        lower *= radix;
                        lower -= Character.digit(ch, radix);
                    } else {
                        upper *= radix;
                        upper -= Character.digit(ch, radix);
                    }
                } else if ( ch == '-' && !inUpper ) {
                    inUpper = true;
                    if ( s.charAt(idx + i + 1) == '-' ) {
                        upperNeg = true;
                    }
                } else if ( ch == ',' ) {
                    i++;
                    break; // end of run
                }
            }

            if ( !inUpper ) {
                ranges.add(lowerNeg ? lower : -lower); // add lower
                ranges.add(lowerNeg ? lower : -lower); // add lower again
            } else {
                ranges.add(lowerNeg ? lower : -lower); // add lower
                ranges.add(upperNeg ? upper : -upper); // add upper
            }

            // reset boolean flags
            lowerNeg = false;
            upperNeg = false;
            inUpper = false;

            // start next run
            idx += i;
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
        return toString();
    }

    public int[] elements() {
        return toArray();
    }

    public boolean equal(IntSpan supplied) {
        return equals(supplied);
    }

    public IntSpan intersection(IntSpan supplied) {
        return intersect(supplied);
    }
}
