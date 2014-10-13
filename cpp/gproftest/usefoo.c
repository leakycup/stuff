//#include <stdio.h>

//extern long double utime, stime;
//int bar(void);

int main(void)
{
    int i;
    int (*p_bar)(void);

    i = foo();

    //p_bar = bar;
    //(*p_bar)();
    bar();

//    printf("utime : %Lf stime : %Lf\n", utime, stime);

    return;
}

int bar(void)
{
#if 0
    unsigned long i = 1000000000;

    while (i)
    {
        i--;
    }
#endif

    return 0;
}

