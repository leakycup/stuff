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
	#echo "dbg: $filename $prefix"
    endif
    if ("$filename" == "") continue
    #echo "dbg: $filename seen"
    echo "$line" | grep '^[>] ' > /dev/null
    if ($status == 0) then # look for lines added diff
	set filename2 = `echo "$line" | sed -e 's/^[>] //' | sed -e 's/ .*//'`
	echo "ADDED: $prefix/$filename/$filename2"
	#echo "ADDED: $filename/$filename2"
    endif
    echo "$line" | grep '^[<] ' > /dev/null
    if ($status == 0) then # look for lines deleted diff
	set filename2 = `echo "$line" | sed -e 's/^[<] //' | sed -e 's/ .*//'`
	echo "DELETED: $prefix/$filename/$filename2"
	#echo "DELETED: $filename/$filename2"
    endif
    
end # foreach line

