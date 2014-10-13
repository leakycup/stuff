#include <stdio.h>
#define MASK32 0x80000000

int main(void)
{
    unsigned long n = 0xf0f0f0f0;
    int j;

    printf("n: ");
    for (j = 0; j < 32; j++)
        printf("%u ", ((n & (MASK32 >> j)) != 0));
    printf("\n");

    n = -n; // n = 2's complement of `n'
    n -= 1; // n = 1's complement of `n'
    printf("~n: ");
    for (j = 0; j < 32; j++)
        printf("%u ", ((n & (MASK32 >> j)) != 0));
    printf("\n");

    return 0;

}

