from datetime import *

dateclass = date
dateobject = dateclass(2015, 2, 14)

print "dateclass: " + str(type(dateclass)) + " value: " + str(dateclass)
print "dateobject: " + str(type(dateobject)) + " value: " + str(dateobject)

class foo(object):
  def __init__(self, message = ""):
    self.message = message
  def prn_instance(self):
    print "from instance method " + self.message
  @classmethod
  def prn_class(cls, message):
    print "from class method " + message
  @staticmethod
  def prn_static(message):
    print "from static method " + message

fooclass = foo
fooobject = fooclass("hi")

print "fooclass: " + str(type(fooclass)) + " value: " + str(fooclass)
print "fooobject: " + str(type(fooobject)) + " value: " + str(fooobject)
print "fooobject.prn_instance: " + str(type(fooobject.prn_instance)) + " value: " + str(fooobject.prn_instance)
print "fooobject.prn_class: " + str(type(fooobject.prn_class)) + " value: " + str(fooobject.prn_class)
print "fooobject.prn_static: " + str(type(fooobject.prn_static)) + " value: " + str(fooobject.prn_static)

fooobject.prn_instance()
fooclass.prn_class("hi")
fooclass.prn_static("hi")

# see https://docs.python.org/2/library/functions.html#classmethod and 
# https://docs.python.org/2/library/functions.html#staticmethod
