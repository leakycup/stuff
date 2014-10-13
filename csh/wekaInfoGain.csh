#!/bin/tcsh -f

java -cp /home/y/share/jports/weka.jar weka.attributeSelection.InfoGainAttributeEval -x 4 $*
