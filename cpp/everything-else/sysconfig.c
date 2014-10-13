#include <stdio.h>

int main(void)
{
    char c;
    short s;
    int i;
    long l;
    long long ll;

    printf("size of long long : %d\n", sizeof(long long) * 8);
    printf("size of long : %d\n", sizeof(long ) * 8);
    printf("size of int : %d\n", sizeof(int ) * 8);
    printf("size of short : %d\n", sizeof(short ) * 8);
    printf("size of char : %d\n", sizeof(char ) * 8);
    printf("\n");

    c = -1;
    if (c < 0) printf("char is signed\n");
    else printf("char is unsigned\n");
    s = -1;
    if (s < 0) printf("short is signed\n");
    else printf("short is unsigned\n");
    i = -1;
    if (i < 0) printf("int is signed\n");
    else printf("int is unsigned\n");
    l = -1;
    if (l < 0) printf("long is signed\n");
    else printf("long is unsigned\n");
    ll = -1;
    if (ll < 0) printf("long long is signed\n");
    else printf("long long is unsigned\n");
    printf("\n");

    printf("size of float : %d\n", sizeof(float) * 8);
    printf("size of double : %d\n", sizeof(double) * 8);
    printf("size of long double : %d\n", sizeof(long double) * 8);
    printf("\n");

    return 0;
}

