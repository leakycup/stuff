#!/bin/tcsh -f

set arffFile = $argv[1]
set modelFile = $arffFile:t.SMOmodel
set Cvalues = "0.0001 0.001 0.01 0.1 1 10 100 1000 10000"

foreach c ($Cvalues)
    #java -cp /home/y/share/jports/weka.jar weka.classifiers.functions.SMO -t $arffFile -d $modelFile-RBF-$c -i -k -x 3 -M -N 0 -C $c -K weka.classifiers.functions.supportVector.RBFKernel >& log.wekaSMO.RBF.$c
    java -cp /home/y/share/jports/weka.jar weka.classifiers.functions.SMO -t $arffFile -d $modelFile-$c -i -k -x 3 -M -N 0 -C $c >& log.wekaSMO.$c
end
