#include <stdio.h>

int main(void)
{
    int a[54];
    int *pa = a;

    printf("size of a = %d\n", sizeof a);
    printf("size of a[0] = %d\n", sizeof a[0]);
    printf("size of pa = %d\n", sizeof pa);
    printf("size of int (*)(void) = %d\n", sizeof(int (*)(void)));
    printf("size of void * = %u\n", sizeof(void *));
}

