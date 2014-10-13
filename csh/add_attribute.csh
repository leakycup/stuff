#!/bin/csh -f

# add_attribute.csh: adds an attribute to a CDETS defect
#
# usage: add_attribute.csh <file> <attribute>
#
# where,
#   'file' is a comma separated list of defects;
#   'attribute' is the attribute to add
#
# example 'file':
#bgl-lds-006:/users/sobhatta/temp>cat not_mcp_collapsegater 
#CSCte92975,CSCti22929,CSCti22955,CSCth64199,CSCth82697,CSCti05493
#bgl-lds-006:/users/sobhatta/temp>
#
# if creating a file with a list of DDTSes seems too cumbersome, consider
# the following hack:
# echo "ddts1,ddts2,...,ddtsn" | add_attribute.csh - <attribute>
# it works!

if ($#argv < 2) then
    echo "usage: add_attribute.csh <file> <attribute>"
    exit 1
endif

set file = $1
set attr = $2

fixcr -S -i "`cat $file`" Attribute $attr

