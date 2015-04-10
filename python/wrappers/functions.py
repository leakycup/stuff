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

def main():
  ivebeencalled()
  ivenotbeencalled()

if __name__ == "__main__":
  main()
