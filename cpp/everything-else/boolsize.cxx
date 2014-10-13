/*
 * sizeof bool in Solaris :
 * if gcc version is < 3, sizeof(bool) is 4.
 * sizeof(bool) is 1 otherwise.
 * */
#include <stdio.h>

int main(void)
{
    printf("sizeof(bool) : %u\n", sizeof(bool));
    return 0;
}

