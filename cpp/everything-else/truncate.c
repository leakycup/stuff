/*
        Input arguments: <filename> <count>
        Prints <count> number of bytes from <filename> to stdout
*/

#include <stdio.h>
#include <errno.h>
#include <stdlib.h>

#define BUF_SIZE 0x100000   //1MB
static char buf[BUF_SIZE];

int main(int argc, char *argv[])
{
        FILE *file;
        unsigned long count, n;

        if (argc != 3)
        {
                fprintf(stderr, "Usage: %s <filename> <count>\n",
                                argv[0]);
                fprintf(stderr, "Prints <count> charecters from"
                                " <filename>\n");
                exit(1);
        }

        file = fopen(argv[1], "r");
        if (!file)
        {
                perror(argv[1]);
                exit(1);
        }

        errno = 0;
        count = strtoul(argv[2], (char **)NULL, 0);
        if (errno)
        {
                perror(argv[2]);
                exit(1);
        }

        for (n = 0; n < count/BUF_SIZE; n++)
        {
                fread(buf, sizeof(buf[0]), BUF_SIZE, file);
                fwrite(buf, sizeof(buf[0]), BUF_SIZE, stdout);
        }
        fread(buf, sizeof(buf[0]), count%BUF_SIZE, file);
        fwrite(buf, sizeof(buf[0]), count%BUF_SIZE, stdout);

        return 0;
}

