
/* ==========================================================================
   Bit Counting routines

   Author: Gurmeet Singh Manku    (manku@cs.stanford.edu)
   Date:   27 Aug 2002
   URL : http://www-db.stanford.edu/~manku/bitcount.html
   ========================================================================== */


#include <stdlib.h>
#include <stdio.h>
#include <limits.h>

/* Iterated bitcount iterates over each bit. The while condition sometimes helps
   terminates the loop earlier */
/* # of iterations is (# of bits in n - # of leading 0s in n) --soubhik*/
int iterated_bitcount (unsigned int n)
{
    int count=0;    
    while (n)
    {
        count += n & 0x1u ;    
        n >>= 1 ;
    }
    return count ;
}


/* Sparse Ones runs proportional to the number of ones in n.
   The line   n &= (n-1)   simply sets the last 1 bit in n to zero. */
/* # of iterations = # of 1s in n. --soubhik*/
int sparse_ones_bitcount (unsigned int n)
{
    int count=0 ;
    while (n)
    {
        count++ ;
        n &= (n - 1) ;     
    }
    return count ;
}


/* Dense Ones runs proportional to the number of zeros in n.
   It first toggles all bits in n, then diminishes count repeatedly */
/* # of iterations = # of 0s in n. --soubhik*/
int dense_ones_bitcount (unsigned int n)
{
    int count = 8 * sizeof(int) ;   
    n ^= (unsigned int) -1 ;
    while (n)
    {
        count-- ;
        n &= (n - 1) ;    
    }
    return count ;
}


/* Precomputed bitcount uses a precomputed array that stores the number of ones
   in each char. */
static int bits_in_char [256] ;

void compute_bits_in_char (void)
{
    unsigned int i ;    
    for (i = 0; i < 256; i++)
        bits_in_char [i] = iterated_bitcount (i) ;
    return ;
}

int precomputed_bitcount (unsigned int n)
{
    // works only for 32-bit ints
    
    return bits_in_char [n         & 0xffu]
        +  bits_in_char [(n >>  8) & 0xffu]
        +  bits_in_char [(n >> 16) & 0xffu]
        +  bits_in_char [(n >> 24) & 0xffu] ;
}


/* Here is another version of precomputed bitcount that uses a precomputed array
   that stores the number of ones in each short. */

static char bits_in_16bits [0x1u << 16] ;

void compute_bits_in_16bits (void)
{
    unsigned int i ;    
    for (i = 0; i < (0x1u<<16); i++)
        bits_in_16bits [i] = iterated_bitcount (i) ;
    return ;
}

int precomputed16_bitcount (unsigned int n)
{
    // works only for 32-bit int
    
    return bits_in_16bits [n         & 0xffffu]
        +  bits_in_16bits [(n >> 16) & 0xffffu] ;
}


/* Parallel   Count   carries   out    bit   counting   in   a   parallel
   fashion.   Consider   n   after    the   first   line   has   finished
   executing. Imagine splitting n into  pairs of bits. Each pair contains
   the <em>number of ones</em> in those two bit positions in the original
   n.  After the second line has finished executing, each nibble contains
   the  <em>number of  ones</em>  in  those four  bits  positions in  the
   original n. Continuing  this for five iterations, the  64 bits contain
   the  number  of ones  among  these  sixty-four  bit positions  in  the
   original n. That is what we wanted to compute. */

/* Added by soubhik :
 * Imagine `n', a 32 bit word, as a 16 pairs of consecutive bits.
 * the operation "n = COUNT(n, 0) ;" converts n to a 32 bit word,
 * such that each pair in the transformed `n' contains the number of bits 
 * in the original pair.
 * A bit pair in original `n'       Corresponding bit pair in the transformed `n'
 * ==========================       =============================================
 * 00                                00
 * 01                                01
 * 10                                01
 * 11                                10
 * the 2nd operation "n = COUNT(n, 1) ;" transforms `n' into a 32 bit word,
 * such that each of the 8 nibbles of transformed `n' stores the number of
 * bits of the corresponding nibble in the original `n'.
 * in more general terms, a sequence of transformations,
 *  n = COUNT(n, 0) ;
 *  n = COUNT(n, 1) ;
 *  ...
 *  n = COUNT(n, i) ;
 *  where i < logBase2(sizeof(n) * 8), converts `n' into chunks of 2 ** (i + 1)
 *  consecutive bits, so that each chunk in the transformed `n' stores the 
 *  number of bits in the corresponding chunk of the original `n'.
 *
 *  MASK(c) : MASK(c) is a 32 bit word, consisting of chunks of 2 ** (c + 1)
 *  consecutive bits. 1st 2 ** c bits of a chunk are all 0s and last 2 ** c
 *  bits are all 1s. thus, MASK(0) contains 16 pairs of bits 01, MASK(1) contains
 *  8 nibbles of bits 0011, and so on. the beauty of this algo lies in the 
 *  formula for computing MASK(c), from a given c.
 *
 *  COUNT(x,c) : COUNT(x,c) creates a word consisting of chunks of 2 ** (c + 1)
 *  consecutive bits, such that each chunk represents the sum of 1st and 2nd halves 
 *  of the corresponding chunk in `x'. Thus COUNT(x,0) is a word consisting of pairs
 *  of consecutive bits, such that each pair is a sum of the 1st and 2nd bits of the
 *  corresponding pair in `x'. Similarly, COUNT(x,2) is a word consisting of bytes, 
 *  such that each byte is a sum of the 1st and 2nd nibble of the corresponding byte 
 *  in `x'. the formula for computing COUNT(x,c) is pretty straight forward.
 * */

#define TWO(c) (0x1u << (c))
#define MASK(c) (((unsigned int)(-1)) / (TWO(TWO(c)) + 1u))
#define COUNT(x,c) ((x) & MASK(c)) + (((x) >> (TWO(c))) & MASK(c))

int parallel_bitcount (unsigned int n)
{
    n = COUNT(n, 0) ;
    n = COUNT(n, 1) ;
    n = COUNT(n, 2) ;
    n = COUNT(n, 3) ;
    n = COUNT(n, 4) ;
    /* n = COUNT(n, 5) ;    for 64-bit integers */
    return n ;
}


/* Nifty  Parallel Count works  the same  way as  Parallel Count  for the
   first three iterations. At the end  of the third line (just before the
   return), each byte of n contains the number of ones in those eight bit
   positions in  the original n. A  little thought then  explains why the
   remainder modulo 255 works. */

#define MASK_01010101 (((unsigned int)(-1))/3)
#define MASK_00110011 (((unsigned int)(-1))/5)
#define MASK_00001111 (((unsigned int)(-1))/17)

int nifty_bitcount (unsigned int n)
{
    n = (n & MASK_01010101) + ((n >> 1) & MASK_01010101) ;
    n = (n & MASK_00110011) + ((n >> 2) & MASK_00110011) ;
    n = (n & MASK_00001111) + ((n >> 4) & MASK_00001111) ;        
    return n % 255 ;
}

/* MIT Bitcount

   Consider a 3 bit number as being
        4a+2b+c
   if we shift it right 1 bit, we have
        2a+b
  subtracting this from the original gives
        2a+b+c
  if we shift the original 2 bits right we get
        a
  and so with another subtraction we have
        a+b+c
  which is the number of bits in the original number.

  Suitable masking  allows the sums of  the octal digits  in a 32 bit  number to
  appear in  each octal digit.  This  isn't much help  unless we can get  all of
  them summed together.   This can be done by modulo  arithmetic (sum the digits
  in a number by  molulo the base of the number minus  one) the old "casting out
  nines" trick  they taught  in school before  calculators were  invented.  Now,
  using mod 7 wont help us, because our number will very likely have more than 7
  bits set.   So add  the octal digits  together to  get base64 digits,  and use
  modulo 63.   (Those of you  with 64  bit machines need  to add 3  octal digits
  together to get base512 digits, and use mod 511.)
 
  This is HACKMEM 169, as used in X11 sources.
  Source: MIT AI Lab memo, late 1970's.
*/

int mit_bitcount(unsigned int n)
{
    /* works for 32-bit numbers only */
    register unsigned int tmp;
    
    tmp = n - ((n >> 1) & 033333333333) - ((n >> 2) & 011111111111);
    return ((tmp + (tmp >> 3)) & 030707070707) % 63;
}

void verify_bitcounts (unsigned int x)
{
    int iterated_ones, sparse_ones, dense_ones ;
    int precomputed_ones, precomputed16_ones ;
    int parallel_ones, nifty_ones ;
    int mit_ones ;
    
    iterated_ones      = iterated_bitcount      (x) ;
    sparse_ones        = sparse_ones_bitcount   (x) ;
    dense_ones         = dense_ones_bitcount    (x) ;
    precomputed_ones   = precomputed_bitcount   (x) ;
    precomputed16_ones = precomputed16_bitcount (x) ;
    parallel_ones      = parallel_bitcount      (x) ;
    nifty_ones         = nifty_bitcount         (x) ;
    mit_ones           = mit_bitcount           (x) ;

    if (iterated_ones != sparse_ones)
    {
        printf ("ERROR: sparse_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }
    
    if (iterated_ones != dense_ones)
    {
        printf ("ERROR: dense_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }

    if (iterated_ones != precomputed_ones)
    {
        printf ("ERROR: precomputed_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }
        
    if (iterated_ones != precomputed16_ones)
    {
        printf ("ERROR: precomputed16_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }
    
    if (iterated_ones != parallel_ones)
    {
        printf ("ERROR: parallel_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }

    if (iterated_ones != nifty_ones)
    {
        printf ("ERROR: nifty_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }

    if (mit_ones != nifty_ones)
    {
        printf ("ERROR: mit_bitcount (0x%x) not okay!\n", x) ;
        exit (0) ;
    }
    
    return ;
}

/*
 * Added by soubhik--
 *
 *  Which of the several bit counting routines is the fastest? 
 *  Results of speed trials on an i686 are summarized in the table. 
 *  "No Optimization" was compiled with plain gcc. 
 *  "Some Optimizations" was gcc -O3. "Heavy Optimizations" 
 *  corresponds to gcc -O3 -mcpu=i686 -march=i686 -fforce-addr 
 *  -funroll-loops -frerun-cse-after-loop -frerun-loop-opt 
 *  -malign-functions=4. 
 *
 *   No Optimization         Some Optimization       Heavy Optimization
 *
 *    Precomp_16 52.94 Mcps    Precomp_16 76.22 Mcps    Precomp_16 80.58 Mcps  
 *    Precomp_8 29.74 Mcps     Precomp_8 49.83 Mcps     Precomp_8 51.65 Mcps
 *    Parallel 19.30 Mcps      Parallel 36.00 Mcps      Parallel 38.55 Mcps
 *    MIT 16.93 Mcps           MIT 17.10 Mcps           Nifty 31.82 Mcps
 *    Nifty 12.78 Mcps         Nifty 16.07 Mcps         MIT 29.71 Mcps
 *    Sparse  5.70 Mcps        Sparse 15.01 Mcps        Sparse 14.62 Mcps
 *    Dense  5.30 Mcps         Dense  14.11 Mcps        Dense 14.56 Mcps
 *    Iterated  3.60 Mcps      Iterated  3.84 Mcps      Iterated  9.24 Mcps
 *
 *    (Mcps = Million counts per second)
 *    Source : see the URL at the top of this file.
 *
 * */


int main (void)
{
    int i ;
    
    compute_bits_in_char () ;
    compute_bits_in_16bits () ;

    verify_bitcounts (UINT_MAX) ;
    verify_bitcounts (0) ;
    
    for (i = 0 ; i < 100000 ; i++)
        verify_bitcounts (lrand48 ()) ;
    
    printf ("All bitcounts seem okay!\n") ;
    
    return 0 ;
}
