from wrappers import *

@print_time
@print_func_name
def ivebeencalled():
# wrappers are called in the order they're specified
  print "I have been called"

@dont_call
def ivenotbeencalled():
# a wrapper need not call the original function
  assert True, "unexpected: I've been called"

@Holi(first = "yellow", second = "red")
def ivebeencoloredtwice():
# meta decorator: one that takes arguments
  print "I've been colored twice"

@Holi(first = "green")
def ivebeencoloredonce():
# meta decorator arguments can be optional too
  print "I've been colored once"

@timeit
def ivebeentimed():
# a decorator that returns a class rather than a method
  print "I've been timed"

def main():
  ivebeencalled()
  ivenotbeencalled()
  ivebeencoloredtwice()
  ivebeencoloredonce()
  wrapper_class = ivebeentimed # represents the class WrapperExecutor
  print "from main(): wrapper_class is " + str(type(wrapper_class))
  wrapper_object1 = wrapper_class() # represents an instance of WrapperExecutor
  print "from main(): wrapper_object1 is " + str(type(wrapper_object1))
  wrapper_object1.execute()
  wrapper_object2 = wrapper_class("hi") # represents an instance of WrapperExecutor
  print "from main(): wrapper_object2 is " + str(type(wrapper_object2))
  wrapper_object2.execute()

if __name__ == "__main__":
  main()
