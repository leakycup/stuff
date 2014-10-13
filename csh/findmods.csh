#!/bin/tcsh -f
#svn stat | grep -v '^[?]'
git st -s --porcelain | grep -v '^[?][?]'
