#!/bin/tcsh -f

# set union $1 ∩ $2

cat $1 $2 | sort -u
