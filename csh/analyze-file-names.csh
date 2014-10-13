#!/bin/csh -f
# given a build log, it extracts the files compiled and
# the component they come from
# argument1: build log

set logfile = $1

foreach line ("`grep -E '^gcc' $logfile`")
    echo $line | grep -E '[.]o$' > /dev/null
    if ($status == 0) continue #ignore .o files
    set filename = `echo $line | sed -e 's/^.*[ ]//'`
    #dbg: echo $filename
    set filenametail = $filename:t
    #dbg: echo $filenametail
    echo $filename | grep -E 'cisco[.]comp' > /dev/null
    if ($status) then
	set component = monolith
    else
	set component = `echo $filename | sed -e 's/^.*cisco[.]comp[/]//' | sed -e 's/[/].*$//'`
    endif
    echo "$filename,$filenametail,$component"
end
