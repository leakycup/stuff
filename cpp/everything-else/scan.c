#include <stdio.h>

int main(void)
{
    int i, j;

	printf("Input format : <number><single char><number>\n");
	printf("`single char' may include space, tab or newline\n");
    scanf("%d%*c%d", &i, &j);
    printf("i : %d j : %d\n", i, j);
    return 0;
}

