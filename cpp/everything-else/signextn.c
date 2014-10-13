#include <stdio.h>
#define MASK8 0x80
#define MASK32 0x80000000

int main(void)
{
    signed char c = 0xff;
    unsigned char d = 0xff;
    signed long i;
    int j;

    printf("c : signed char. d : unsigned char. i : signed long\n\n");

    printf("c : ");
    for (j = 0; j < 8; j++)
        printf("%u ", ((c & (MASK8 >> j)) != 0));
    printf("\n");

    printf("d : ");
    for (j = 0; j < 8; j++)
        printf("%u ", ((d & (MASK8 >> j)) != 0));
    printf("\n");

    i = c;
    printf("(i = c): ");
    for (j = 0; j < 32; j++)
        printf("%u ", ((i & (MASK32 >> j)) != 0));
    printf("\n");

    i = d;
    printf("(i = d): ");
    for (j = 0; j < 32; j++)
        printf("%u ", ((i & (MASK32 >> j)) != 0));
    printf("\n");

    return 0;

}

