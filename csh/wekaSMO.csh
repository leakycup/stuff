#!/bin/tcsh -f

set arffFile = $argv[1]
set modelFile = $arffFile:t.SMOmodel

#java -cp /home/y/share/jports/weka.jar weka.classifiers.functions.SMO -t $arffFile -d $modelFile -s `date +'%s'` -i -k -x 3 -M -N 2
java -cp /home/y/share/jports/weka.jar weka.classifiers.functions.SMO -t $arffFile -d $modelFile -i -k -x 3 -M -N 0
