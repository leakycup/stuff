def function_with_optional_args(arg1, optinal_arg=None):
  print 'required: {0}'.format(arg1)
  if optinal_arg:
    print 'optional: {0}'.format(optinal_arg)
  print '---------------------------------'

def function_with_variable_positional_args(arg1, *args):
  print 'required: {0}'.format(arg1)
  print 'optional (as list): {0}'.format(args)
  for arg in args:
    print 'optional: {0}'.format(arg)
  print '---------------------------------'

def function_with_variable_named_args(arg1, **kwargs):
  print 'required: {0}'.format(arg1)
  print 'optional (as dict): {0}'.format(kwargs)
  for arg in kwargs:
    print 'optional: {0} : {1}'.format(arg, kwargs[arg])
  print '---------------------------------'

def function_with_variable_args(arg1, *args, **kwargs):
  print 'required: {0}'.format(arg1)
  print 'optional positional args (as list): {0}'.format(args)
  for arg in args:
    print 'optional positional arg: {0}'.format(arg)
  print 'optional named args (as dict): {0}'.format(kwargs)
  for arg in kwargs:
    print 'optional named arg: {0} : {1}'.format(arg, kwargs[arg])
  print '---------------------------------'

#bad: 2nd arg passed in an invokation may be assigned to optional_arg, or kwargs
def function_with_optional_and_variable_args(arg1, optional_arg=None, *args, **kwargs):
  print 'required: {0}'.format(arg1)
  if optional_arg:
    print 'optional: {0}'.format(optional_arg)
  print 'optional positional args (as list): {0}'.format(args)
  for arg in args:
    print 'optional positional arg: {0}'.format(arg)
  print 'optional named args (as dict): {0}'.format(kwargs)
  for arg in kwargs:
    print 'optional named arg: {0} : {1}'.format(arg, kwargs[arg])
  print '---------------------------------'

if __name__ == '__main__':
  print '---------------------------------'

  function_with_optional_args('function_with_optional_args: call#1')
  function_with_optional_args('function_with_optional_args: call#2', 'optional arg')

  function_with_variable_positional_args('function_with_variable_positional_args: call#1')
  function_with_variable_positional_args('function_with_variable_positional_args: call#2', 'positional arg1')
  function_with_variable_positional_args('function_with_variable_positional_args: call#3', 'positional arg1', 'positional arg2')

  function_with_variable_named_args('function_with_variable_named_args: call#1')
  function_with_variable_named_args('function_with_variable_named_args: call#2', name1='named arg1')
  function_with_variable_named_args('function_with_variable_named_args: call#3', name1='named arg1', name2='named arg2')

  function_with_variable_args('function_with_variable_args: call#1')
  function_with_variable_args('function_with_variable_args: call#2', 'positional arg1', 'positional arg2')
  function_with_variable_args('function_with_variable_args: call#3', name1='named arg1', name2='named arg2')
  function_with_variable_args('function_with_variable_args: call#4', 'positional arg1', 'positional arg2', name1='named arg1', name2='named arg2')

  function_with_optional_and_variable_args('function_with_optional_and_variable_args: call#1', '2nd arg')
  function_with_optional_and_variable_args('function_with_optional_and_variable_args: call#2', optional_arg='2nd arg')
  function_with_optional_and_variable_args('function_with_optional_and_variable_args: call#3', second_arg='2nd arg')
  #function_with_optional_and_variable_args('function_with_optional_and_variable_args: call#4', optional_arg='2nd arg', '3rd arg') # SyntaxError: non-keyword arg after keyword arg
  function_with_optional_and_variable_args('function_with_optional_and_variable_args: call#5', '2nd arg', '3rd arg')
