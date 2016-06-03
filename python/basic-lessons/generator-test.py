mylist1 = xrange(10) # generator

myevennumbers = filter(lambda x: x%2 == 0, mylist1) # filter on a generator
print type(myevennumbers) # list

print 'evens'
for n in myevennumbers:
  print str(n)

mylist2 = [1, 2, 3, 4] # list comprehension
mylist3 = [2*n for n in mylist2] # list comprehension
mylist4 = (2*n for n in mylist2) # generator
mylist5 = list(2*n for n in mylist2) # generator + list constructor

print 'not divisible by 3'
myoddnumbers = filter(lambda x: x%3, mylist4)
for n in myoddnumbers:
  print str(n)
