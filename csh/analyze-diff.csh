#!/bin/csh -f

set diff_filename = $1
set filename = ""
set prefix = ""
set header_only = "trivial"

foreach line ("`cat $diff_filename`")
    echo "$line" | grep '^Index[:] ' > /dev/null
    if ($status == 0) then # found a file name in diff
	if ("$filename" != "") then
	    echo "$prefix/$filename : $header_only"
	endif
	set filename = `echo "$line" | sed -e 's/^Index[:] //'`
	echo "$filename" | egrep '^sys[/]|^deprecated[/]' > /dev/null
	if ($status == 0) then
	    set prefix = "/vob/ios"
	else
	    set prefix = "/vob/cisco.comp"
	endif
	set header_only = "trivial"
	#echo "dbg: $filename $prefix"
    endif
    if ("$filename" == "") continue
    if ("$header_only" == "non-trivial") continue
    #echo "dbg: $filename seen"
    echo "$line" | grep '^[\!+-][ ]' > /dev/null
    if ($status) continue # ignore context, other meta information
    #echo "dbg: $filename : a diff line seen"
    echo "$line" | grep '^[\!+-][ \t]*[#]include[ \t]' > /dev/null
    if ($status == 0) continue # ignore header inclusion changes
    echo "$line" | grep '^[\!][ ][ ][*][ ]Copyright[ ]' > /dev/null
    if ($status == 0) continue # ignore copyright changes
    #echo "dbg: $filename : a non-trivial diff line seen"
    #echo "dbg: $line"
    set header_only = "non-trivial"
end # foreach line

if ("$filename" != "") then
    echo "$prefix/$filename : $header_only"
endif
