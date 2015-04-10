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

def main():
  ivebeencalled()
  ivenotbeencalled()
  ivebeencoloredtwice()
  ivebeencoloredonce()

if __name__ == "__main__":
  main()
