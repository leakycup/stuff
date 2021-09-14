import sys 
import codecs
import numpy as np

#UTF8Writer = codecs.getwriter('utf8')
#sys.stdout = UTF8Writer(sys.stdout)

input_file = sys.argv[1]
probabilities_file = sys.argv[2]
sample_size = int(sys.argv[3])

input_list = []
probabilities_list = []

with codecs.open(input_file, 'r', 'utf-8') as f:
    for line in f:
      input_list.append(line.strip())

with codecs.open(probabilities_file, 'r', 'utf-8') as f:
    for line in f:
        probabilities_list.append(float(line.strip()))

for line in np.random.choice(input_list, p=probabilities_list, size=sample_size, replace=False):
  print (line)
