#!/bin/csh -f

#how to execute a command which is 
#an output of command/variable substitution?
#eval
set cmd = ls
eval `echo $cmd`
eval $cmd
``echo $cmd``  #doesn't work

#how to display an environment variable which is
#an output of variable substitution?
#printenv
setenv var 1
setenv varname var
printenv $varname
printenv `echo $varname`  #doesn't work. why??
echo $$varname   #doesn't work : parsing occurs b4 substitution

