/////////////////////////////////////
//chars.c
//prints all the ascii charecters on terminal,
//along with their unsigned integer value.
//////////////////////////////////////


#include <stdio.h>

int main (void)
{
    int i = 0;
    for (i=0; i<255; i++)
    {
        printf("%d : %c\n", i, i);
    }
    return 0;
}

