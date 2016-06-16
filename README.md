[![Travis](https://img.shields.io/travis/egateam/jintspan.svg)](https://travis-ci.org/egateam/jintspan)
[![Codecov branch](https://img.shields.io/codecov/c/github/egateam/jintspan/master.svg)](https://codecov.io/github/egateam/jintspan?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.egateam/jintspan.svg)](http://search.maven.org/#search|ga|1|g%3A%22com.github.egateam%22%20AND%20a%3A%22jintspan%22)

# NAME

`IntSpan` handles of sets containing integer spans.

## SYNOPSIS

```
import com.github.egateam.IntSpan;

IntSpan set = new IntSpan();
set.add(new int[]{1, 2, 3, 5, 7, 9});
set.addPair(100, 10000);
set.remove(1000);
System.out.println(set.toString()); // 1-3,5,7,9,100-999,1001-10000
```

## DESCRIPTION

The class `IntSpan` represents sets of integers as a number of inclusive ranges, for example
`1-10,19-23,45-48`. Because many of its operations involve linear searches of the list of ranges its
overall performance tends to be proportional to the number of distinct ranges. This is fine for
small sets but suffers compared to other possible set representations (bit vectors, hash keys) when
the number of ranges grows large.

This module also represents sets as ranges of values but stores those ranges in order and uses a
binary search for many internal operations so that overall performance tends towards O log N where N
is the number of ranges.

The internal representation used by this module is extremely simple: a set is represented as a list
of integers. Integers in even numbered positions (0, 2, 4 etc) represent the start of a run of
numbers while those in odd numbered positions represent the ends of runs. As an example the set (1,
3-7, 9, 11, 12) would be represented internally as (1, 2, 3, 8, 11, 13).

Sets may be infinite - assuming you're prepared to accept that infinity is actually no more than a
fairly large integer. Specifically the constants `negINF` and `posINF` are defined to be (-2^31+1)
and (2^31-2) respectively. To create an infinite set invert an empty one:

```
IntSpan infSet = new IntSpan().invert();
```

Sets need only be bounded in one direction - for example this is the set of all positive integers
(assuming you accept the slightly feeble definition of infinity we're using):

```
IntSpan posInfSet = new IntSpan();
posInfSet.addPair(1, IntSpan.getPosInf());
```

This Java class is ported from the Perl module `AlignDB::IntSpan` which contains many codes from
`Set::IntSpan`, `Set::IntSpan::Fast` and `Set::IntSpan::Island`.

## DOCUMENTS

* Github pages: http://egateam.github.io/jintspan/apidocs/index.html

## COMPARISON

* ArrayList<Integer>

```
$ mvn clean verify
$ command time -l java -jar target/jintspan-*.jar file 50
step 1 create
duration 1.200802
step 2 intersect
duration 17.512677
step 3 intersect runlist
duration 20.821079
       39.65 real        40.48 user         0.30 sys
 402702336  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    101852  page reclaims
         0  page faults
         0  swaps
         0  block input operations
        10  block output operations
         0  messages sent
         0  messages received
         2  signals received
         0  voluntary context switches
     33186  involuntary context switches
```

* IntArrayList from hppc

```
$ mvn clean verify
$ command time -l java -jar target/jintspan-*-jar-with-dependencies.jar file 50
step 1 create
duration 1.293469
step 2 intersect
duration 16.917867
step 3 intersect runlist
duration 20.511756
       38.84 real        39.33 user         0.29 sys
 504291328  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    125893  page reclaims
         1  page faults
         0  swaps
         0  block input operations
         7  block output operations
         0  messages sent
         0  messages received
         2  signals received
         0  voluntary context switches
     31818  involuntary context switches
```

## AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

## COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself. For details, see the full text of the license in the file
LICENSE.
