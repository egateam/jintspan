/*
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package org.egateam;

import java.util.ArrayList;

public class IntSpan {
    private static String emptyString = "-";

    // Real Largest int is posInf - 1
    private static int posInf = 2147483647 - 1; // INT_MAX - 1
    private static int negInf = (-2147483647 - 1) + 1; // INT_MIN + 1

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

    public IntSpan(String runlist) {
        runlist = stripWhitespace(runlist);

        // empty set
        if ( runlist.equals("") || runlist.equals(emptyString) ) {
            // Do nothing
        } else {
            addRange(runlistToRanges(runlist));
        }
    }

    //----------------------------------------------------------
    // Set contents
    //----------------------------------------------------------
    public ArrayList<Integer> edges() {
        return edges;
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

    public boolean isEmpty() {
        return edgeSize() == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean isNegInf() {
        return edges.get(0) == negInf;
    }

    public boolean isPosInf() {
        return edges.get(edges.size() - 1) == posInf;
    }

    public boolean isInfinite() {
        return isNegInf() || isPosInf();
    }

    public boolean isFinite() {
        return !isInfinite();
    }

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

    public IntSpan add(int n) {
        addPair(n, n);

        return this;
    }

    public IntSpan merge(IntSpan supplied) {
        ArrayList<Integer> ranges = supplied.ranges();
        this.addRange(ranges);

        return this;
    }

    //----------------------------------------------------------
    // Set operations ( create new set)
    //----------------------------------------------------------
    public IntSpan copy() {
        IntSpan newSet = new IntSpan();

        newSet.edges = new ArrayList<Integer>(edges);

        return newSet;
    }

    public IntSpan union(IntSpan supplied) {
        IntSpan newSet = this.copy();
        newSet.merge(supplied);

        return newSet;
    }

    //----------------------------------------------------------
    // Set comparisons
    //----------------------------------------------------------

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

}
