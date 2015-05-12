#!/bin/tcsh -f

# set intersection $1 ∩ $2

grep -F -x -f $1 $2
