#!/bin/tcsh -f

set numObjective = `grep 'OBJECTIVE$' training.arff | wc -l`
set numSubjective = `grep 'SUBJECTIVE$' training.arff | wc -l`
set ratio = `echo "$numObjective / $numSubjective" | bc -l`
echo $ratio

sed -e 's/OBJECTIVE$/&,{1}/' -e 's/SUBJECTIVE$/&,{'$ratio'}/' training.arff > weighted-training.arff

wekaSMO.csh training.arff >& log.wekaSMO.1
wekaSMO.csh weighted-training.arff >& log.wekaSMO.2

evalWekaSMO.csh training.arff.SMOmodel test.arff >& log.evalWekaSMO.1
evalWekaSMO.csh weighted-training.arff.SMOmodel test.arff >& log.evalWekaSMO.2
