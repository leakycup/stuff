#!/bin/tcsh -f

set arffFile = $argv[1]
set modelFile = $arffFile:t.NBmodel

java -cp /home/y/share/jports/weka.jar weka.classifiers.bayes.NaiveBayes -t $arffFile -d $modelFile -s `date +'%s'` -i -k
