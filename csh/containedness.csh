#!/bin/tcsh -f

# how contained is $1 in $2 ?

set i = `intersection.csh $1 $2 | wc -l`
set d = `cat $1 | wc -l`

echo "$i / $d" | bc -l
