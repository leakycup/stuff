#!/bin/tcsh -f

set urlfile = "$1"
set urlprefix = "$2"
set sleep = "$3"
set outputfile = "$4"
set num_procs = "$5"

set script_dir = "$0:h"

set proc = 0
set num_lines = `wc -l "$urlfile"`
#echo "sbh: num_lines: $num_lines"
@ num_lines /= "$num_procs"
#echo "sbh: num_lines_per_proc: $num_lines num_procs: $num_procs"
while ($proc < $num_procs)
  @ start = 1 + "$proc" * "$num_lines"
  #echo "sbh: start: $start"
  $script_dir/simple-loadtest.csh "$urlfile" "$urlprefix" "$sleep" "$outputfile.$start" "$start" "$num_lines" &
  @ proc += 1
end
