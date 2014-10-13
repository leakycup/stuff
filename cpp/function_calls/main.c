//main module
//compile with and without
//-Doldc and see the difference
//in output

int main(void)
{
    return caller();
}

/* OBS :
    * System : GNU C version 2.96 20000731 (Red Hat Linux 7.1 2.96-98) (i386-redhat-linux)

    * Scenarios :
      =========
    Formal    OldC    Actual    Caller Passes    Callee Expects
    ======----====----======----=============----==============
1.  long       Y/N     long        long            long
    long               long        long            long
    
2.  long       Y       long/       long            long
    long               short/                      long
                       char

3.  long       N       long/       long            long
    long               short/      long            long
                       char

4. long/      Y/N      long/       long             long
   short/              short/
   char                char

5. long/       Y       long        long             long
   short/              long        long
   char

6. long/       N       long        long             long
   short/              long
   char

   In scenarios 2 and 5, what caller passes is different from
   what callee expects and are responsible for wrong output.
*/

