#!/bin/csh -f

# prints this script, one word per line
echo "++++ printing myself : one word per line ++++"
foreach word (`cat $0`)
echo $word
end
echo "++++ printing myself : done ++++"

# prints this script, one line per line
echo "++++ printing myself : one line per line ++++"
foreach line ("`cat $0`")
echo $line
end
echo "++++ printing myself : done ++++"

