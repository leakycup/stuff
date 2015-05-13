#!/bin/tcsh -f

# set difference $1 - $2

foreach l ("`cat $1`")
  grep -F -x "$l" $2 > /dev/null
  if ($status != 0) echo "$l"
end
