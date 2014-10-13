/* result of shift operations (<< and >>) are undefined if 
 * the right operand (shift amount) is greater than or equal
 * to the number of bits in the left expressions type.
 * This program illustrates it.
 * I've built and tested it with gcc-3.2.3 on RedHat Linux 7.2.
 * */

#include <stdio.h>

int main(void)
{
    int size = 4;
    int mask;

    mask = (1 << 8*size);
    printf("mask : 0x%x\n", mask); /* prints 0x1 */
    mask = (1 << 8*4);  /* compiler warning */
    printf("mask : 0x%x\n", mask); /* prints 0x0 */

    return 0;
}

