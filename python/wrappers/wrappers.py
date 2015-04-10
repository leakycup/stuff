from datetime import *

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
