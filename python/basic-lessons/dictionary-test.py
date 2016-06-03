mydict = {1:2, 2:4, 3:6, 4:8}
print 'keys'
for k in mydict: #iterator over dict returns keys
  print str(k)
items = (item for item in mydict.iteritems()) # generator of (key,value) pairs
filtered_items = filter(lambda x: x[1]%4, items) #list of filtered (key,value) pairs
final_dict = dict(filtered_items) #dict made from filtered (key,value) pairs
print 'filtered items'
for item in final_dict.iteritems(): # iterator over (key,value) pairs
  print str(item[0]) + "=>" + str(item[1])
