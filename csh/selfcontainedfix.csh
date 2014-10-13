#!/bin/csh -f

# this script reports the diffs committed against each bugid committed between two
# versions of a given component. it also analyzes the monolithic diffs and speculates
# if it is a self-contained fix.
# the output is several lines, each line for a bugid committed between the 'from' and
# 'to' versions. format of each line is as follows:
#<component>,<bugid>,<list of diff enclosures separated by spaces>,<whether self-contained>

#arguments
if ($#argv != 3) then
    echo "USAGE: $0 <component> <from-version> <to-version>"
    echo ""
    echo "component: name of a true component"
    echo "from-version: a version of the component where to start the analysis from"
    echo "to-version: a version of the component where to end the analysis to"
    echo ""
    echo "a component version is expressed as (prefix)major.minor.implementation"
    exit 1
endif
set component = $1
set from = $2
set to = $3

# tools
set ciscobin = /usr/cisco/bin
set cc_list_bugs = $ciscobin/cc_list_bugs
set findcr = $ciscobin/findcr
set dumpcr = $ciscobin/dumpcr

# files / directories
set pubman = .publication_manifest
set comp_vob = /vob/ss.comp1

echo "from version:$from,to version:$to"
foreach bugid (`$cc_list_bugs -from "version:$from" -to "version:$to" -vob $comp_vob -component $component`) # for each fix between the 'from' and 'to' versions

    set diffs = `$findcr -i $bugid -w 'Attachment-title,Note-title' | tr ',' '\n' | grep '^Diffs--'`

    set isselfcontained = "Potentially self-contained"
    foreach d ($diffs) # for each diff enclosure attached to the fix
	echo $d | grep "^Diffs--comp_" > /dev/null # ignore component diff
	if ($status == 0) continue
	$dumpcr -e -a $d -u $bugid | grep "^Index" | grep -v $pubman > /dev/null
	if ($status == 0) then
	    set isselfcontained = "Potentially Non-self-contained ($d)"
	    break
	endif
    end

    echo "$component,$bugid,$diffs,$isselfcontained" #spit it out
end

