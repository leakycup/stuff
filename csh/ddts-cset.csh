#!/bin/csh -f

set diff_filename = $1
set filename = ""
set prefix = ""

foreach line ("`cat $diff_filename`")
    echo "$line" | grep '^Index[:] ' > /dev/null
    if ($status == 0) then # found a file name in diff
	set filename = `echo "$line" | sed -e 's/^Index[:] //'`
	echo "$filename" | egrep '^sys[/]|^deprecated[/]' > /dev/null
	if ($status == 0) then
	    set prefix = "/vob/ios"
	else
	    set prefix = "/vob/cisco.comp"
	endif
	echo "$prefix/$filename"
    endif
end # foreach line

