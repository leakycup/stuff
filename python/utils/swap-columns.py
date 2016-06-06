import codecs
import sys

filename = sys.argv[1]
f = codecs.open(filename, "r", "utf-8")

UTF8Writer = codecs.getwriter('utf8')
sys.stdout = UTF8Writer(sys.stdout)
sys.stderr = UTF8Writer(sys.stderr)

for line in f:
  try:
    lhs,rhs = line.strip().split("\t")
  except ValueError as e:
    print >> sys.stderr, line.strip() + ": " + str(e)
  print rhs + "\t" + lhs
