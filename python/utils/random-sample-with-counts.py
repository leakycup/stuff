import sys 
import codecs
import random

#UTF8Writer = codecs.getwriter('utf8')
#sys.stdout = UTF8Writer(sys.stdout)

input_file = sys.argv[1]
counts_file = sys.argv[2]
sample_size = int(sys.argv[3])

input_list = []
counts_list = []

with codecs.open(input_file, 'r', 'utf-8') as f:
    for line in f:
      input_list.append(line.strip())

with codecs.open(counts_file, 'r', 'utf-8') as f:
    for line in f:
        counts_list.append(int(line.strip()))

# requires python 3.9
for line in random.sample(input_list, counts=counts_list, k=sample_size):
  print (line)

