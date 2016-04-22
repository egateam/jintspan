[![Build Status](https://travis-ci.org/wang-q/jintspan.svg?branch=master)](https://travis-ci.org/wang-q/jintspan)
[![codecov.io](https://codecov.io/github/wang-q/jintspan/coverage.svg?branch=master)](https://codecov.io/github/wang-q/jintspan?branch=master)

# NAME

`IntSpan` handles of sets containing integer spans.

## SYNOPSIS

```
import com.github.egateam.IntSpan;

IntSpan set = new IntSpan();
set.add(new int[]{1, 2, 3, 5, 7, 9});
set.addPair(100, 10000);
set.remove(1000);
System.out.println(set.asString()); // 1-3,5,7,9,100-999,1001-10000
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

## AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

## COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself.
