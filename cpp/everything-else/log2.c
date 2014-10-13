#include <stdio.h>

int main(void)
{
    unsigned int number, n;
    register int i = 0;

    printf("Input a natural number :\n");
    scanf("%d", &number);
    printf("\n");

    n = number;
    while ( (n >>= 1) != 0)
        i++;

    printf("|_log2(%u)_| = %d\n", number, i);
    printf("\n");

    i = 0;
    for (n = number - 1; n != 0; n >>= 1)
        i++;
    
    printf("Ceiling of log2(%u) = %d\n", number, i);
    printf("\n");

    printf("Number of bits needed to represent\n");
    printf("numbers in the range 0 to %u is %d\n", (number - 1), i);

    return 0;
}

