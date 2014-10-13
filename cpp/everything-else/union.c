/* This code demonstrates some basic differences between
 * struct and union
 * */

#include <stdio.h>

typedef struct {
    unsigned int i;
    unsigned char c;
} myType1;

typedef union {
    unsigned int i;
    unsigned char c;
} myType2;

int main(void)
{
    myType1 o1;
    myType2 o2;

    printf ("in struct o1 (%p) : address of i : %p address of c : %p\n",
            &o1, &o1.i, &o1.c);
    printf ("in union  o2 (%p) : address of i : %p address of c : %p\n",
            &o2, &o2.i, &o2.c);

    printf("sizeof struct o1 : %u\n", sizeof(o1));
    printf("sizeof union  o2 : %u\n", sizeof(o2));

    return 0;
}

