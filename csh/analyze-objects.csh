#!/bin/csh -f
# analyze a list of object files and determine the static memory
# requirement for each.
# argument1: a text file which is a list of object files, one per line
# argument2: binutils version (tested with c3.4.5-p5)

set objlist = $1
set ver = $2
set readelf = readelf.$ver

set totsize = 0

foreach obj (`cat $objlist`)
    #echo $obj
    set objtail = $obj:t
    set objsize = 0
    foreach line ("`$readelf -S $obj | grep '^  \[' | grep -v '^  \[Nr'`")
	#echo "$obj": "$line"
	set flag = `echo "$line" | cut -b '76-80'`
	set size = `echo "$line" | cut -b '66-72' | tr '[:lower:]' '[:upper:]'`
	set name = `echo "$line" | cut -b '8-25'`
	#echo "$obj $name $flag $size"
	echo $flag | grep A > /dev/null
	if ($status) continue
	@ objsize += `echo "ibase=16; value=$size; print value" | bc -l`
    end
    echo "$objtail,$obj,$objsize"
    @ totsize += $objsize
end
echo "Total : $totsize"

