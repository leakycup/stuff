//this program tests the endianness of the system.
//if the last 4 integers printed by it are 1, 0, 0, 0
//then it is a little endian system. if they are 0, 0,
//0, 1 then it is a big endian system.
//note that the 1st 2 integers printed by it are same
//and are 1, 0, irrespective of the endianness.

#include <stdio.h>

int main(void)
{
    int r;
    char *p;

    r = 1;

    printf("%d\n", ( (r & 1) != 0));
    printf("%d\n", ( (r & (1 << 31)) != 0));

    for (p = (char *)&r; p < (((char *)&r) + 4); p++)
        printf("%c\n", (*p + '0'));

    return 0;
}

