#!/bin/tcsh -f

set u = `union.csh $1 $2 | wc -l`
set i = `intersection.csh $1 $2 | wc -l`

echo "$i / $u" | bc -l
