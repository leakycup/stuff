/*
tty input a 32-bit unsigned integer.
tty outputs a input number of 0s. can be redirected to 
create a file of size as specified in input.
*/
#include <stdio.h>

int main(void)
{
    unsigned long count = 268435456; //256MB
    unsigned long i;

    //read count
    scanf("%lu", &count);

    for (i = 0; i < count; i++)
        printf("%c", 0);

    return 0;
}

