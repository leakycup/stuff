import sys 
import codecs
import random

UTF8Writer = codecs.getwriter('utf8')
sys.stdout = UTF8Writer(sys.stdout)

input_file = sys.argv[1]
sample_size = int(sys.argv[2])

input_list = []

with codecs.open(input_file, 'r', 'utf-8') as f:
    for line in f:
        input_list.append(line)

for line in random.sample(input_list, sample_size):
    print line,
