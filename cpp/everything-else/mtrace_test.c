//what happens if some memory is allocated *before* calling
//`mtrace()' but freed *after* calling it?
//`mtrace' reports that there's an attempt to free an
//unallocated memory.

#include <stdlib.h>
#include <mcheck.h>

int main(void)
{
    int *p = malloc(15);
    mtrace();
    free(p);
    return 0;
}

