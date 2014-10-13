/* output of this program is different if compiled as a C program
 * and if compiled as a C++ program */

#include <stdio.h>

int main(void)
{
    int s = sizeof ('a');
    printf("%u\n", s);
}

