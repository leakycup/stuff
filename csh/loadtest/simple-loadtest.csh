#!/bin/tcsh -f

set urlfile = "$1"
set urlprefix = "$2"
set sleep = "$3"
set outputfile = "$4"
set start = "$5" #inclusive
set num_lines = "$6"

set end = `echo "$start + $num_lines - 1" | bc`
#echo "sbh: start: $start end: $end"

touch "$outputfile"
foreach url ("`head -n $end $urlfile | tail -n $num_lines`")
  echo "$urlprefix$url" >> "$outputfile"
  curl -g -s -S "$urlprefix$url" >>& "$outputfile"
  set rand = `echo foo | awk 'BEGIN { srand() } {print rand() }'`
  if (`echo "$rand < 0.5" | bc`) then
    sleep "$sleep"
  endif
end
