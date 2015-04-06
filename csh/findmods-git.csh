#!/bin/tcsh -f
git st -s --porcelain | grep -v '^[?][?]'
