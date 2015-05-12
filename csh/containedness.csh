#!/bin/tcsh -f

# how contained is $1 in $2 ?

set i = `intersection.csh $1 $2 | wc -l`

echo "$i / $1" | bc -l
