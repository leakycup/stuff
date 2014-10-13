#include <string.h>

int main(void)
{
    char c[50];

    printf("Empty : %s\n", "");
    printf("NULL : %s\n", NULL);
    strcpy(c, NULL);  //seg fault

    return 0;
}

