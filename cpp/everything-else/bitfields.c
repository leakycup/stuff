#include <stdio.h>

#define MIN(a, b) ( (a) <= (b) ? (a) : (b) )

#define OpBitField(arg, lsb, msb) \
        ( ( (unsigned int)(arg) & ( (((unsigned int)1<<(lsb))-1) ^ (((unsigned int)1<<(msb+1))-1) ) ) >> MIN((unsigned char)(lsb), (unsigned char)(msb)) )

int main(void)
{
    unsigned int lsb, msb, word;
    unsigned int mask = ~0x0;
    unsigned int value;

    printf("Read bit-field\n");
    printf("**************\n");
    printf("lsb (0 to 31) ?\n");
    scanf("%u", &lsb);
    printf("msb (0 to 31 and >= lsb) ?\n");
    scanf("%u", &msb);
    printf("word (int) ?\n");
    scanf("%u", &word);

    printf("Original word: ");
    do {
        int i;
        for (i = 0; i < 32; i++)
            printf("%u ", ((word & ((unsigned) 0x80000000 >> i)) != 0));
        printf("\n");
    } while (0);

    //mask >>= ((sizeof word) * 8 - msb - 1);
    //word &= mask;
    //word >>= lsb;

    word = OpBitField(word, lsb, msb);

    printf("Selected word: ");
    do {
        int i;
        for (i = 0; i < 32; i++)
            printf("%u ", ((word & ((unsigned) 0x80000000 >> i)) != 0));
            //printf("%u ", ((( (((unsigned int)1<<(lsb))-1) ) & ((unsigned) 0x80000000 >> i)) != 0));
        printf("\n");
    } while (0);

    /************************************************/

    printf("Write bit-field\n");
    printf("***************\n");
    printf("lsb (0 to 31) ?\n");
    scanf("%u", &lsb);
    printf("msb (0 to 31 and >= lsb) ?\n");
    scanf("%u", &msb);
    printf("word (int) ?\n");
    scanf("%u", &word);
    printf("value ?\n");
    scanf("%u", &value);

    printf("Original word: ");
    do {
        int i;
        for (i = 0; i < 32; i++)
            printf("%u ", ((word & ((unsigned) 0x80000000 >> i)) != 0));
        printf("\n");
    } while (0);

    mask = ~0x0;
    mask >>= (sizeof (int) * 8 - msb -1);
    mask &= ~((int) 0x0) << lsb;

    value <<= lsb;
    value &= mask;

    mask = ~mask;
    word &= mask;

    word |= value;

    printf("Modified word: ");
    do {
        int i;
        for (i = 0; i < 32; i++)
            printf("%u ", ((word & ((unsigned) 0x80000000 >> i)) != 0));
        printf("\n");
    } while (0);

    return 0;

}

