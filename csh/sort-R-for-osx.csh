#! /bin/csh -f

cat - | awk 'BEGIN { srand() } { print rand() "\t" $0 }' | sort -n | cut -f2-
