# bare basics: variables, loops, conditionals, constants
# define classes, methods and objects
# symbolic constants (macros / enums)
# Library:
# regular expression, find, replace and other text utilities
# open/close/read from/write to a file
# command line argument processing
# parsing xml, html, json
# open/close/read from/write to a URI
# reading / modifying "media" objects: jpeg, mp3, mpeg


puts 3
puts 1+2
puts 'large number:'
puts 1024 * 1024 * 1024 * 4 * 2 * 1024
puts 'Hello world'
puts 'escape \' and \\'
puts 'repeat me thrice ' * 3
puts 'add me ' + 'with an empty string' + ''

name = 'Prince Stephen Abraham Jeyasingh ponraj'
puts name + ' is a long name'

expr = '32 * 59 / 7'
puts expr
puts 'this is a number (partially evaluated expr): '
puts expr.to_i
expr = 32 * 59 / 7
puts expr

puts 'this is a number: ' + expr.to_s

#puts 'Hi ' + gets.chomp + ', how do you do today?'

#puts 'Hello ' + gets.chomp + '!'  # learning ruby

#puts 'get me a number:'
#number = gets.chomp
#number = number.to_i + 1
#puts 'this is your lucky number today: ' + number.to_s

#puts 'enter your first name'.capitalize
#first = gets.chomp
#puts 'enter your last name'.capitalize
#last = gets.chomp
#puts 'first name \'' + first.capitalize + '\' has ' + first.length.to_s + ' charecters'
#puts 'last name \'' + last.capitalize + '\' has ' + last.length.to_s + ' charecters'

linewidth = 30
str = '> test <'
puts str.center linewidth
puts (str.ljust(linewidth) + str.rjust(linewidth))
#puts str.ljust linewidth + str.rjust linewidth
#
# In an object instance variable (denoted with '@'), remember a block.
def remember(&a_block)
  @block = a_block
end
 
# Invoke the above method, giving it a block which takes a name.
remember {|name| puts "Hello, #{name}!"}
 
# When the time is right (for the object) -- call the closure!
@block.call("Jon")
# => "Hello, Jon!"

# currying:
# create a more specific version of a generalized function
def div_gen(x)
	return lambda {|y| return y/x}
end

half = div_gen(2)
third = div_gen(3)

puts "66/2 == " + half.call(66).to_s
puts "66/3 == " + third.call(66).to_s

# Proc vs Method
myproc = Proc.new {"this is s proc"}
mymethod = lambda {"this is a method"}

puts myproc.call
puts mymethod.call

puts "myproc is " + myproc.class.to_s
puts "mymethod is " + mymethod.class.to_s

def what_am_i(&block)
  block.class
end

puts what_am_i {}

myproc = Proc.new {|x,y| puts "I can add: " + (x + y).to_s}
myproc.call(4,5)
myproc.call(4,5,6) # no arg check

mymethod = lambda {|x,y| puts "I can add: " + (x + y).to_s}
mymethod.call(3,4)
#mymethod.call(4,5,6) # syntax error

def return_test(code)
	code.call()
	puts "going on"
end

mymethod = lambda {return "mymethod returns"} # 'return' allowed within method
return_test(mymethod)

#myproc = Proc.new {return "myproc returns"} # syntax error
#puts return_test(myproc)

def return_frm_proc
	puts (Proc.new {return "return from Proc"}).call # 'return' is allowed in a Proc, sometimes.. hack. beaware: the result may be very unexpected
	puts "back to caller"
end
return_frm_proc

def return_frm_method
	puts (lambda {return "return from Method"}).call
	puts "back to caller"
end
return_frm_method

# the notions of an anonymous function (lambda), first class function and 
# closure revolve around
# an unique capability of a functional programming language: that of 
# dynamic code generation.
# a language supports first class functions if it has the built-in capabilities to construct a function dynamically, store it in memory, pass it as an argument to another function and return it as a value from a function.
# all of the above capabilities are natively available in a moduler programming language like C, via the use of a 'function pointer' or 'function reference'; except that of construction of a function dynamically.
# support for a first class function is usually built on top of the following built-in primitives of a functional programming language:
# 1. anonymous function aka a function literal
# 2. a closure which is an enclosed function object, which contains the function text and the storage for the private state the body of the function has references to. a closure may be viewed as a specific instance of a generic (parameterized) function, with the values of the parameters fully specified. parameters refer to the private state of the function.
# 3. an operator to construct a closure from an anonymous function (lambda is viewed both as an operator to construct a closure as well as the anonymous function itself)
# 4. a function variable aka a variable which holds a reference to a closure
# a language with first class functions offer a distinct way of
# 1. designing software and
# 2. expressing a program
# though they have the exactly same overall capability as that of a moduler programming language like C. i.e. anything that can be achieved using first class functions can be achieved using a moduler programming language, and vice versa, albeit the design and look and feel of the code will be different.
# why is this interesting?
# in web programming, a server often generates code which is sent to the client side and client executes the code. this is considered better than client invoking an API function on the server side over a remote procedule call mechanics, since it avoids the compatibility issue between the client and the server. so a language with built in support for code generation fits more naturally into this requirement.
# without this support, the server side program will use of the following:
# 1. code templates which are viewed as texts. these don't benefit from compilation / syntax check / other code analysis tooling.
# 2. server side program generates a call to a generic API function, where the generic function resides on server side. the code snippet sent to client essentially calls the generic server side function with specific parameters. this can lead to greater network traffic and loss of efficiency and reliability.
# 3. the server side may send the code for the generic function along with the private state such that the client invokes a specific instance of the generic function. this is what is made implicit and done automatically by a language which supports first class functions.

# deaf grandma program
def yell(words)
	return lambda {puts words.upcase}
end
say_that_again_please = yell('say that again please')
not_since_1949 = yell('not since 1949')
def not_since(year)
	yell('not since ' + year.to_s)
end

puts 'Deaf Grandma Program: say something to the granny'
question = gets.chomp
while question != 'BYE'
	if question == question.upcase
		not_since(rand(69) + 1930).call()
	else
		say_that_again_please.call()
	end
	question = gets.chomp
end

