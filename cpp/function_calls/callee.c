//compile this file (upto assembly level) to see
//how the callee functions
//find their parameters.
//try compiling with and without -Doldc

#include <stdio.h>

long long I = 0, J = 0;
long K = 0, L = 0;
short M = 0, N = 0;
char O = 0, P = 0;

#ifdef oldc
int callee1(i, j)
long long i, j;
#else
int callee1(long long i, long long j)
#endif
{
    I = i;
    J = j;
    printf("callee1 : I = %lld J = %lld\n", I, J);
    return 0;
}

#ifdef oldc
int callee2(i, j)
long i, j;
#else
int callee2(long i, long j)
#endif
{
    K = i;
    L = j;
    printf("callee2 : K = %ld L = %ld\n", K, L);
    return 0;
}

#ifdef oldc
int callee3(i, j)
short i, j;
#else
int callee3(short i, short j)
#endif
{
    M = i;
    N = j;
    printf("callee3 : M = %hd N = %hd\n", M, N);
    return 0;
}

#ifdef oldc
int callee4(i, j)
char i, j;
#else
int callee4(char i, char j)
#endif
{
    O = i;
    P = j;
    printf("callee4 : O = %c P = %c\n", O + '0', P + '0');
    return 0;
}

