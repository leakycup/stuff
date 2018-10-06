import sys 
import codecs

UTF8Writer = codecs.getwriter('utf8')
sys.stdout = UTF8Writer(sys.stdout)

strings_file = sys.argv[1]
integers_file = sys.argv[2]

output = []

with codecs.open(strings_file, 'r', 'utf-8') as f:
    for line in f:
        output.append(line)


i = 0
with codecs.open(integers_file, 'r', 'utf-8') as f:
    for line in f:
        l = int(line)
        output[i] = output[i][:l]
        print output[i]
        i += 1
