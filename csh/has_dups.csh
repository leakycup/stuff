#!/bin/tcsh -f

set num_elements = `cat $1 | wc -l`
set num_uniqs = `dedup.csh $1 | wc -l`

@ d = $num_elements - $num_uniqs

echo "$1 : num_elements: $num_elements, num_uniqs: $num_uniqs"
if ($d == 0) exit 0
if ($d > 0) exit 1
echo "internal error: num_elements < num_uniqs"
exit 2
