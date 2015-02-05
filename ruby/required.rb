class Foo
end

# __FILE__ is the name of the current source file
# $0 or $PROGRAM_NAME at the top level is the name of the top-level program being executed
if __FILE__ == $PROGRAM_NAME # $PROGRAM_NAME is the less cryptic name for $0
   # this block of code executes if and only if this ruby program is invoked
   # directly rather than being imported via a 'require' (see requiree.rb)
   puts "Testing out class Foo"
end
