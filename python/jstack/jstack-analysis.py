import sys
import re
from collections import defaultdict

stack_file = sys.argv[1]
num_elements_to_print = int(sys.argv[2])
f = open(stack_file, 'r')
element_count = defaultdict(int)
component_count = defaultdict(int)

runnable = False
prev = None
for line in f:
  line = line.strip()
  if not line:
    runnable = False
    continue
  if 'java.lang.Thread.State: RUNNABLE' in line:
    runnable = True
    continue
  if runnable:
    element = line.replace('at ', '')
    element_count[element] += 1
    if 'org.apache.solr.handler.component.SearchHandler.handleRequestBody' in element:
      component_count[prev] += 1
    prev = re.sub('[.][^.(]*[(].*[)]$', '', element)

print '---------- stack element count --------------'
for element in sorted(element_count, key=element_count.get, reverse=True)[:num_elements_to_print]:
  print element, element_count[element]

print ''
print '---------- component count --------------'
for component in sorted(component_count, key=component_count.get, reverse=True):
  print component, component_count[component]

