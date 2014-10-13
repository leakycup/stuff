#include <stdio.h>
     #include <setjmp.h>
     #include <signal.h>
     #include <unistd.h>
     jmp_buf env; static void signal_handler();

     static void signal_handler(sig)
     int sig; {
             switch (sig)     {
               case SIGINT:          /* process for interrupt */
                              longjmp(env,sig);
                                     /* break never reached */
               case SIGALRM:         /* process for alarm */
                              longjmp(env,sig);
                                    /* break never reached */
               default:       exit(sig);
             }
     }

     main()  {
             int returned_from_longjump, processing = 1;
             unsigned int time_interval = 4;
         unsigned int interrupt_count = 5, alarm_count = 5;
             (void) signal(SIGINT, signal_handler);
             (void) signal(SIGALRM, signal_handler);
             alarm(time_interval);
             if ((returned_from_longjump = setjmp(env)) != 0)
                 switch (returned_from_longjump)     {
                   case SIGINT:
                     printf("longjumped from interrupt %d\n",SIGINT);
             interrupt_count--;
                     break;
                   case SIGALRM:
                     printf("longjumped from alarm %d\n",SIGALRM);
             alarm_count--;
                     break;
                 }
             while (interrupt_count && alarm_count)        {
               printf(" waiting for you to INTERRUPT (cntrl-C) ...\n");
               sleep(1);

           }       /* end while forever loop */
     }

