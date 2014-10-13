#include <stdio.h>
#include <time.h>
#include <sys/times.h>
#include <unistd.h>

int main(void)
{
    unsigned int i;
    time_t start, end;
    struct tms start_ex, end_ex;
    unsigned long utime, stime;

    times(&start_ex);
    start = time(NULL);

    for (i = 0; i < (unsigned int)(-1); i++)
        ;

    end = time(NULL);
    times(&end_ex);

    utime = (unsigned long)(end_ex.tms_utime - start_ex.tms_utime);
    stime = (unsigned long)(end_ex.tms_stime - start_ex.tms_stime);
    utime /= (unsigned long)sysconf(_SC_CLK_TCK);
    stime /= (unsigned long)sysconf(_SC_CLK_TCK);

    printf("resp time = %lus\n", (end - start));
    printf("exec time : utime = %lus stime = %lus\n", utime, stime);

    return 0;
}

