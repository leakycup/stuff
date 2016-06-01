import codecs
import sys

filename = sys.argv[1]
f = codecs.open(filename, "r", "utf-8")

UTF8Writer = codecs.getwriter('utf8')
sys.stdout = UTF8Writer(sys.stdout)

for line in f:
  print line.lower(),
