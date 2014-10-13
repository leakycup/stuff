#include <sys/times.h>
#include <unistd.h>

long double utime, stime;

int foo(void)
{
    struct tms start, end;
    unsigned long i = 1000000000;

    times(&start);

    while (i)
    {
        i--;
        printf(" \n");
    }

    times(&end);
    utime = (long double)(end.tms_utime - start.tms_utime);
    stime = (long double)(end.tms_stime - start.tms_stime);
    utime /= (long double)sysconf(_SC_CLK_TCK);
    stime /= (long double)sysconf(_SC_CLK_TCK);

    return i;
}

