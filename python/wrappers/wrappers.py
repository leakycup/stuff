from datetime import *

'''
see http://hangar.runway7.net/python/decorators-and-wrappers : a good tutorial on python wrappers/decorators
'''

def print_func_name(func):
  def print_name_and_call(*args, **kwargs):
    print "calling function: %s" % func.__name__
    return func(*args, **kwargs)
  return print_name_and_call

def print_time(func):
  def print_time_and_call(*args, **kwargs):
    print "time now: %s" % datetime.today()
    return func(*args, **kwargs)
  return print_time_and_call

def dont_call(func):
  def dont_call_func(*args, **kwargs):
    print "skipping : %s" % func.__name__
  return dont_call_func

class Holi(object):
  def __init__(self, first, second = "none"):
    self.first = first
    self.second = second

  def __call__(self, func):
    def print_colors_and_call(*args, **kwargs):
      print "first: " + self.first + ", second: " + self.second
      return func(*args, **kwargs)
    return print_colors_and_call

class GeneralExecutor(object):
  def __init__(self, message = ""):
    self.message = message

  def execute(self):
    print self.message
    start = datetime.utcnow()
    self.execute_func()
    end = datetime.utcnow()
    elapsed_time = end - start
    print "elapsed time: %s" % elapsed_time

  def execute_func(self):
    raise NotImplementedError()

def timeit(func):
  class WrapperExecutor(GeneralExecutor):
    def execute_func(self):
      #return func(self, *self.args, **self.kargs)
      return func()
  print "from timeit(): " + str(type(WrapperExecutor))
  return WrapperExecutor
