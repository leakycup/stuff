#!/bin/tcsh -f

set arffFile = $argv[1]
set modelFile = $arffFile:t.SLmodel

java -cp /home/y/share/jports/weka.jar weka.classifiers.functions.SimpleLogistic -t $arffFile -d $modelFile -s `date +'%s'` -i -k -M 100 -I 100
