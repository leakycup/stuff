#!/bin/csh -f

set model = $argv[1]
set testArff = $argv[2]

java -cp /home/y/share/jports/weka.jar weka.classifiers.Evaluation weka.classifiers.functions.SMO -l $model -T $testArff -o -v -i
