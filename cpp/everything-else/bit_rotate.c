#include <stdio.h>

unsigned long MyRightRotate(unsigned long word, unsigned short rot, unsigned short n);
unsigned long OpRightRotate(unsigned long arg, unsigned short no_bits, unsigned short size);
unsigned long MyLeftRotate(unsigned long word, unsigned short rot, unsigned short n);
    unsigned long OpLeftRotate(unsigned long arg, unsigned short no_bits, unsigned short size);

int main(void)
{
    unsigned long word;
    unsigned short rot, n;

    printf("word : ");
    scanf("%lu", &word);
    printf("rot : ");
    scanf("%hu", &rot);
    printf("n : ");
    scanf("%hu", &n);

    printf("right-rotated value : %lu\n", OpRightRotate(word, rot, n));
    printf("right-rotated value : %lu\n", MyRightRotate(word, rot, n));

    printf("left-rotated value : %lu\n", OpLeftRotate(word, rot, n));
    printf("left-rotated value : %lu\n", MyLeftRotate(word, rot, n));

    return 0;
}

//right rotates lower `n' bits of `word' by an amount `rot'
//leaves upper (sizeof(word)*8 - n) bits of `word' unchanged
unsigned long MyRightRotate(unsigned long word, unsigned short rot, unsigned short n)
{
    unsigned long m, w, hi, lo;
    unsigned short r;

    r = rot % n;
    m = word & (((unsigned long) (-1)) << n);
    w = word & ~(((unsigned long) (-1)) << n);

    hi = w & (((unsigned long) (-1)) << r);
    lo = w & ~(((unsigned long) (-1)) << r);

    hi >>= r;
    lo <<= (n - r);

    //return (hi | lo);  //this resets upper ((sizeof(word)*8 - n) 
                         //bits of `word' to 0s
    return ((hi | lo) | m);  //this keeps them unchanged
}

//right rotates lower `n' bits of `word' by an amount `rot'
//resets upper (sizeof(word)*8 - n) bits of `word' to 0s
    unsigned long OpRightRotate(unsigned long arg, unsigned short no_bits, unsigned short size){
        
        unsigned long val=0,mask=1,and_mask = ~0;
        unsigned short i;

        mask <<= (size - 1);    /* Mask at MSB */
        val = arg;

        and_mask >>= (32 - size);

        for(i=0;i<no_bits;++i){

            if(val & 1){
                val >>= 1;
                val |= mask;  /* Rotating right */
            }
            else{
                val >>= 1;
                val &= ~mask;
            }

        }

        val &= and_mask;

        return(val);

    }

//left rotates lower `n' bits of `word' by an amount `rot'
//leaves upper (sizeof(word)*8 - n) bits of `word' unchanged
unsigned long MyLeftRotate(unsigned long word, unsigned short rot, unsigned short n)
{
    unsigned long m, w, hi, lo;
    unsigned short r;

    r = rot % n;
    r = n - r;

    return MyRightRotate(word, r, n);  //right-rotate `r' bits is same 
                                       //as left-roatte (n - r) bits.
}

//left rotates lower `n' bits of `word' by an amount `rot'
//resets upper (sizeof(word)*8 - n) bits of `word' to 0s
    unsigned long OpLeftRotate(unsigned long arg, unsigned short no_bits, unsigned short size){
        
        unsigned long val=0,mask=1,and_mask = ~0;
        unsigned short i;

        mask <<= (size - 1); /* Putting mask at MSB */
        val = arg;

        and_mask >>= (32 - size);

        for(i=0;i<no_bits;++i){

            if(val & mask){
                val <<= 1;     /* Rotating to left */
                val |= 1;
            }
            else
                val <<= 1;

        }

        val &= and_mask;

        return(val);

    }

        
