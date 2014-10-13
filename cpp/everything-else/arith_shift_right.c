//the follwoing code shows how to
//do an arithmetic shift right in C

int main(void)
{
    unsigned int i = -1;
    signed int j = -1;
    int is_negative_i, is_negative_j;

    //right shifts as defined in C
    i >>= 3;  //logical shift
    j >>= 3;  //implementation defined shift

    printf("Right shifts as in the present implementation\n");
    printf("i : ");
    do {
        int ii;
        for (ii = 0; ii < 32; ii++)
            printf("%u ", ((i & ((unsigned) 0x80000000 >> ii)) != 0));
        printf("\n");
    } while (0);

    printf("j : ");
    do {
        int ii;
        for (ii = 0; ii < 32; ii++)
            printf("%u ", ((j & ((unsigned) 0x80000000 >> ii)) != 0));
        printf("\n");
    } while (0);

    //arithmetic right shifts
    i = -1;
    j = -1;

    is_negative_i = ((signed int)i < 0); //msb of `i'
    i >>= 3; //logical shift
    i |= (!is_negative_i) ? 0 : (-1 << (sizeof(int)*8 - 3));
    is_negative_j = (j < 0); //msb of `j'
    (unsigned int)j >>= 3;  //logical shift
    j |= (!is_negative_j) ? 0 : (-1 << (sizeof(int)*8 - 3));

    printf("\n");
    printf("Arithmetic right shifts\n");
    printf("i : ");
    do {
        int ii;
        for (ii = 0; ii < 32; ii++)
            printf("%u ", ((i & ((unsigned) 0x80000000 >> ii)) != 0));
        printf("\n");
    } while (0);

    printf("j : ");
    do {
        int ii;
        for (ii = 0; ii < 32; ii++)
            printf("%u ", ((j & ((unsigned) 0x80000000 >> ii)) != 0));
        printf("\n");
    } while (0);

    return 0;
}

