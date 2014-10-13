/*
 * Reads a text file which contains hexadecimal integers from stdin
 * and dumps it as a binary file in stdout.
 * Typical usage : cat Input-file | This-program >  Output-file
 * sample input file (top left lowest address, bottom right highest address) :
 
 2009ADF4 2009AC24 65C15B30 60610000  C3D6F58 61EEAC60        0 20DF6E40
        4 64540000 641D0000 20DF6E70 60B6B01C        0 2009AC24 2009AC24
      266 500E4ABC 62257DD0 622581E8 AC10AF01 60B2EC08 64C70000 500E4ABC
       18 61ED80C4        0 60B61240        0        1        0 2009AE84
 65C15948 2009B850 2009AC24 61EEA5EC 2009AC24        0      266 500E4ABC

 */

#include <stdio.h>

int main(void)
{
    int num = 1, ret;
    char *p = &num;
    int le_host = 0, le_source = 0, reverse = 0;

    if (*p == 1) {
	le_host = 1;
	fprintf(stderr, "Host : Little Endian\n");
    } else {
	fprintf(stderr, "Host : Big Endian\n");
    }
    fprintf(stderr, "A == %x a == %x 1 == %x\n", 'A', 'a', '1');
    
    reverse = (le_source == le_host) ? 0 : 1;

    while ((ret = scanf("%x", &num)) && (ret != EOF)) {
	char temp;

	if (reverse) {
	    int i;
	    char *q;

	    p = &num;
	    q = p + sizeof(int) - 1;
	    for (i = 0; i < sizeof(int)/2; i++) {
		temp = *p;
		*p = *q;
		*q = temp;
		p++;
		q--;
	    }
	}
	write(1, &num, sizeof(int));
    }

     return (0);
}

