#!/bin/tcsh -ef

set start = $1
set end = $2
set file = $3

set n = $end
@ n -= $start - 1

#echo "end: $end n: $n"
head -n $end $file | tail -n $n
