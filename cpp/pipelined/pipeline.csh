#!/bin/csh -f

echo "===================================="
set shell_var=1

setenv PIPELINE_TEST 1
echo PIPELINE_TEST : $PIPELINE_TEST
echo sourcing child.csh without pipe
source child.csh
echo PIPELINE_TEST : $PIPELINE_TEST

echo "===================================="

setenv PIPELINE_TEST 1
echo PIPELINE_TEST : $PIPELINE_TEST
echo sourcing child.csh inside a pipe
source child.csh | tee /dev/null
echo PIPELINE_TEST : $PIPELINE_TEST

echo "===================================="

echo "moral : pipelined commands execute in a sub-shell."
echo "but then why a shell variable defined in parent "
echo "visible to the child?"

