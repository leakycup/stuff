//assemble this piece of code to see
//where the different variables have been
//allocated

//following comments describe the results
//seen with GCC 2.96 20000731, configured as
//i386-redhat-linux (native compiler), ran as
//gcc -S -g

///////////////////////////////////////////////

//the global variables `g', `pg', `ag',
//`ag[0]', `ag[1]' have immediate constant address
//(constant during execution, assembly code
//uses symbolic constants to access these).
int g;
int *pg;
int ag[2];

int main(void)
{
    //addresses of automatic variables
    //is relative to `ebp'
    int l; //ebp - 4
    int *pl; //ebp - 8
    int al[2]; //ebp - 16 and ebp - 12

    g = 1;  // => 1 store operation, immediate addressing mode
    pg = &g;
    *pg = 2;  // => 1 load, immediate addressing mode, and 1 store, reg indirect addr mode
    ag[0] = 3; // => 1 store, immediate addressing mode
    ag[1] = 4; // => 1 store, immediate addressing mode

    l = 5;  // => 1 store, reg indirect (reg + offset) addr mode
    pl = &l;
    *pl = 6;  // => 1 load, reg indirect (reg + offset) addr mode, 1 store, reg indirect addr mode
    al[0] = 7;  // => 1 store, reg indirect (reg + offset) addr mode
    al[1] = 8;  // => 1 store, reg indirect (reg + offset) addr mode

    //pl = ag;
    printf("ag = %p\n", ag);
    scanf("%d", &pl);
    *(pl + 1) = 9;  //3 instructions, 2 instructions if -O3 is specified
    printf("%d\n", *(pl + 1));

    //l = 1;
    scanf("%d", &l);
    ag[l] = 10;  //5 instructions, 2 instructions if -O3 is specified
    printf("%d\n", ag[l]);

    return 0;
}

