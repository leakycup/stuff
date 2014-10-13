#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(void) {
    int fdlist[2];
    int ret;
    pid_t pid;

    ret = pipe(fdlist);
    if (ret) {
        perror("failed to create a pipe");
        exit(1);
    }

    pid = fork();
    if (pid == 0) { // child process. writer.
        char buf[] = "Hello from child";

        dup2(fdlist[1], 1);
        close(fdlist[1]); // reader gets an EOF even if it is not closed here
        write(1, buf, sizeof(buf));
        //while(1); // reader does not receive EOF if child does not exit
        printf("child exiting\n");
        exit(0);
    }

    // parent process. reader.
    close(fdlist[1]); //reader does not receive EOF if it does not close the unused writer fd
    while (1) {
        char buf[5];
        int bytesRead = read(fdlist[0], buf, sizeof(buf));

        if (bytesRead < 0) {
            perror("failed to read from the pipe");
            exit(1);
        } else if (bytesRead == 0) {
            printf("pipe closed\n");
            break;
        } else {
            printf("parent received: %s\n", buf);
        }
    }

    exit(0);
}
