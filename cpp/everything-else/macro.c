#include <stdio.h>

//a macro, that is (almost) functionally equivalent to a function
//having many simple statements and returning a value.
#define M(a,b) \
((a) = 0, \
(b) = 1, \
(a + b))
//almost equivalent function is as follows:
/*
int M(int a, int b)
{
    a = 0;
    b = 1;
    return (a + b);
}
*/
//The macro and function are NOT exactly equivalent because:
//--side effects : if the macro is used then the expressions
//                 `a' and `b' would be evaluated twice. if 
//                  function is used then this undesirable side-effect 
//                  is not present.
//--type          : in function the types of `a', `b' and the return
//                  type are `int'. in macro, `a' and `b' can have 
//                  any type.
//compound statements and automatic variables : in a macro like this
//                  we can not have compound statements and local 
//                  variables.
//call by reference : if implemented as a function, parameters will be 
//                    passed to M as call by value mechanism. however,
//                    in a macro M, the parameters are passed as call by
//                    reference.


int main(void)
{
    int aa, bb, cc;
    
    cc = M(aa, bb);

    printf("cc = %d\n", cc);
    return 0;
}

