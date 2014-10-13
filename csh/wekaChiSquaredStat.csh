#!/bin/tcsh -f

java -cp /home/y/share/jports/weka.jar weka.attributeSelection.ChiSquaredAttributeEval -x 4 $*
