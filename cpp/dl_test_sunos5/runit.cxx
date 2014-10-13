#include "tests.h"

extern "C" {
#include <stdio.h>
#include <dlfcn.h>
}

int main(void);

int main (void) {
    void *dlhndl = NULL;
    void *sym = NULL;
    helloWorld h1 = helloWorld();
    helloWorld h2 = helloWorld(216);
    int ret;

    dlhndl = dlopen(0, RTLD_NOW | RTLD_GLOBAL);
    if (!dlhndl) {
        printf("%s\n", dlerror());
    }
    sym = dlsym(dlhndl, "main");
    if (!sym) {
        printf("%s\n", dlerror());
    }
    ret = dlclose(dlhndl);
    if (ret) {
        printf("%s\n", dlerror());
    }

    sym = dlsym(RTLD_DEFAULT, "main");
    if (!sym) {
        printf("%s\n", dlerror());
    }

    dlhndl = dlopen(DLL1, RTLD_NOW | RTLD_GLOBAL | RTLD_PARENT);
    if (dlhndl == NULL) {
        printf("%s\n", dlerror());
    }
    else {
        int (*fptr)();
        fptr = (int (*)()) dlsym(dlhndl, "loadThisFunction");
        if (fptr == NULL) {
            printf("%s\n", dlerror());
        }
        else {
            (*fptr)();
        }
        ret = dlclose(dlhndl);
        if (ret) {
            printf("%s\n", dlerror());
        }
    }
    h1.printHello();
    h2.printHello();
    h1.printHello();
    h2.printHello();

    dlhndl = dlopen(DLL2, RTLD_NOW | RTLD_GLOBAL | RTLD_PARENT);
    if (dlhndl == NULL) {
        printf("%s\n", dlerror());
    }
    else {
        int (*fptr)();
        fptr = (int (*)()) dlsym(dlhndl, "loadThisFunction");
        if (fptr == NULL) {
            printf("%s\n", dlerror());
        }
        else {
            (*fptr)();
        }
        ret = dlclose(dlhndl);
        if (ret) {
            printf("%s\n", dlerror());
        }
    }
    h1.printHello();
    h2.printHello();
    h1.printHello();
    h2.printHello();

    return(0);
}
