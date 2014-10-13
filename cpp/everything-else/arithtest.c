#include <stdio.h>

int main(void)
{
    unsigned short s1, s2, s3;
    unsigned long l1, l2, l3;

    printf("sizeof unsigned short : %u\n", sizeof(unsigned short));
    printf("sizeof unsigned long : %u\n", sizeof(unsigned long));

    s1 = (unsigned short) -1;
    s2 = (unsigned short) -1;
    s3 = s1 + s2;
    l1 = (unsigned short)(s1 + s2);
    l2 = (unsigned long)(s1 + s2);
    l3 = s1 + s2;

    /* illustrates integer promotion :
     * l3 == l2 and l3 != l1 */

    printf("s1 : 0x%hx s2 : 0x%hx s3 : 0x%hx \n", s1, s2, s3);
    printf("l1 : 0x%lx l2 : 0x%lx l3 = 0x%lx \n", l1, l2, l3);

    return;
}

