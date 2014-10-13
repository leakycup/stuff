/*
 * This program shows how a 'struct' with a bitfield,
 * whose size is 1 byte, and a 'char' differ.
 * Values of 'obj' and 'cobj', when interpreted as 'int',
 * are different in a SunOS 5.7 (SUNW,Ultra-60) system. 
 * However, values are same on a Linux 2.4.7-10 (i686).
 * Also note that the values printed depend on the endianity
 * of the execution platform.
 * Note that it's risky to mask 'obj' and 'cobj' to (int *)
 * as this may result in alignment problem.
 * */

#include <stdio.h>

int main(void)
{
    struct {
        unsigned char c : 1;
    } obj[] = {{0}, {1}, {0}, {1}};
    unsigned char cobj[] = {0, 1, 0, 1};

    printf("sizeof(obj) : %u\n", sizeof(obj));
    printf("obj == 0x%08x\n", *(int *)obj);

    printf("sizeof(cobj) : %u\n", sizeof(cobj));
    printf("cobj == 0x%08x\n", *(int *)cobj);

    return 0;
}

