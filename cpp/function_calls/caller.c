//assemble to see
//how caller passes the arguments
//to callee functions

#include <stdio.h>

#ifdef oldc
int callee1();
int callee2();
int callee3();
int callee4();
#else
int callee1(long long, long long);
int callee2(long, long);
int callee3(short, short);
int callee4(char, char);
#endif

int caller(void)
{
    long long i = 0, j = 1;
    long k = 2, l = 3;
    short m = 4, n = 5;
    char o = 6, p = 7;

    //calling with proper arguments
    printf("calling with proper args...\n");
    printf("caller : i = %lld j = %lld\n", i, j);
    callee1(i, j);
    printf("caller : k = %ld l = %ld\n", k, l);
    callee2(k, l);
    printf("caller : m = %hd n = %hd\n", m, n);
    callee3(m, n);
    printf("caller : o = %c p = %c\n", o + '0', p + '0');
    callee4(o, p);
    printf("\n");

    //calling with smaller arguments
    printf("calling with smaller args...\n");
    printf("caller : o = %c p = %c\n", o + '0', p + '0');
    callee3(o, p);
    printf("caller : m = %hd n = %hd\n", m, n);
    callee2(m, n);
    printf("caller : k = %ld l = %ld\n", k, l);
    callee1(k, l);
    printf("\n");

    //calling with larger arguments
    printf("calling with larger args...\n");
    printf("caller : i = %lld j = %lld\n", i, j);
    callee2(i, j);
    printf("caller : k = %ld l = %ld\n", k, l);
    callee3(k, l);
    printf("caller : m = %hd n = %hd\n", m, n);
    callee4(m, n);

    return 0;
}

