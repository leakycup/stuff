#!/bin/csh -f

# to checkout branch 'foo' and track remote branch 'foo' from 'origin'
# git-checkout-remote-branch.csh foo
#
# to checkout branch 'bar' and track remote branch 'bar' from 'myfork'
# git-checkout-remote-branch.csh bar myfork
# 

set branch = "$1"

set remote = origin
if ($#argv > 1) then
    set remote = "$2"
endif

git checkout -b "$branch" "$remote"/"$branch"

