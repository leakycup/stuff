/*
 * Computes ((2 to the power N) - 1) using
 * shift operations.
 * */

#include <stdio.h>

int main(void)
{
    unsigned long word = -1;
    int n;

    printf("Enter N (>= 0 and <= %lu) :\n", 8*sizeof(word));
    scanf("%d", &n);
    printf("=====================================\n");

    //method 1 : applies if (n >= 0 && n < 8*sizeof(word))
    if (n >= 0 && n < 8*sizeof(word))
    {
        printf("N : %d >= 0 and < %u\n", n, 8*sizeof(word));
        printf("Using method 1 : left-shift and complement\n");
        printf("Result : %lu (0x%lx)\n", ~(word << n), ~(word << n));
        printf("=====================================\n");
    }

    //method 2 : applies if (n > 0 && n <= 8*sizeof(word))
    if (n > 0 && n <= 8*sizeof(word))
    {
        printf("N : %d > 0 and <= %u\n", n, 8*sizeof(word));
        printf("Using method 2 : right-shift\n");
        printf("Result : %lu (0x%lx)\n", 
                (word >> (8*sizeof(word) - n)), (word >> (8*sizeof(word) - n)));
        printf("=====================================\n");
    }

    //method 3 : applies if (n == 0)
    if (n == 0)
    {
        printf("N : %d == 0\n", n);
        printf("Using method 3\n");
        printf("Result : 0 (0x0)\n");
        printf("=====================================\n");
    }

    //method 4 : applies if (n == 8*sizeof(word))
    if (n == 8*sizeof(word))
    {
        printf("N : %d == %u\n", n, 8*sizeof(word));
        printf("Using method 4\n");
        printf("Result : %lu (0x%lx)\n", word, word);
        printf("=====================================\n");
    }

    return 0;
}

