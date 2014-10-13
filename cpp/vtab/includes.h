#include <stdio.h>
#include <stdlib.h>
#include <string.h>

class employee {
    private :
        char *name;
    public :
        //employee(char *n) { name = strdup(n); }
        //~employee() { free(name); }
        virtual float sal(void) { return 20.00; }
};

class ceo : public employee {
    public:
        //ceo(char *n) : employee(n) {}
        //~ceo() {}
        float sal(void) { return 50.00; }
};

class manager : public employee {
    public:
        //manager(char *n) : employee(n) {}
        //~manager() {}
        float sal(void) { return 35.00; }
};

