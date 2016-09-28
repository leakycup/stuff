import java.lang.reflect.Array;

import java.util.Date;
import java.util.Random;

class InputGenerator {

    private static double degreeToRadian = 0.0174532925;

    public static void main(String[] args) {
        Random randomGenerator = new Random((new Date()).getTime());

        if ((Array.getLength(args) != 4) && (Array.getLength(args) != 7)) {
            System.err.println("***Incorrect arguments.***");
            System.err.println("Usage:");
            System.err.println("\tjava InputGenerator <size> <maxInput> " +
                               "<maxSleepiness> <maxWait>" +
                               " [<magnitude> <width> <numberOfBursts>]");
            System.err.println("\t\t\t<size> : number of inputs to be generated");
            System.err.println("\t\t\t<fileName> : name of the file where " +
                               "generated input data is written");
            System.err.println("\t\t\t<maxInput> : maximum value of the input " +
                               "the srver app has to process");
            System.err.println("\t\t\t<maxSleepiness> : maximum value of the " +
                               "sleepiness of the worker thread");
            System.err.println("\t\t\t<maxWait> : maximum time the client " +
                               "request thread sleeps between two requests");
            System.err.println("\t\t\t<magnitude> : magnitude of bursts");
            System.err.println("\t\t\t<width> : width of bursts");
            System.err.println("\t\t\t<numberOfBursts> : number of bursts");
            return;
        }
        int size = Integer.parseInt(args[0]);
        int maxInput = Integer.parseInt(args[1]);
        int maxSleepiness = Integer.parseInt(args[2]);
        int maxWait = Integer.parseInt(args[3]);
        System.out.println("# size: " + size + ", maxInput: " + maxInput +
                           ", maxSleepiness: " + maxSleepiness +
                           ", maxWait: " + maxWait);
        int width = 0, numberOfBursts = 0;
        double A = 0, B = 0;
        if (Array.getLength(args) == 7) { //burst: y = D - A*sin((B/5)*x)
            A = Double.parseDouble(args[4]); //magnitude = D - A
            width = Integer.parseInt(args[5]);
            B = 180*5 / width; //width = 180*5/B
            numberOfBursts = Integer.parseInt(args[6]);
            System.out.println("# Burst: A: " + A + ", B: " + B +
                               ", numberOfBursts: " + numberOfBursts +
                               ", width of a burst: " + width);
        }
        System.out.println("# <input>,<sleepiness>,<wait>");

        int numbersBetweenBursts = size;
        if (numberOfBursts > 0) {
            numbersBetweenBursts = (size - width*numberOfBursts) / numberOfBursts;
        }
        boolean burstInProgress = false;
        int numbersToBurst = numbersBetweenBursts;
        int x = 0;
        int wait = maxWait;
        double D = 0;
        while (size-- > 0) {
            int input = randomGenerator.nextInt(maxInput);
            int sleepiness = randomGenerator.nextInt(maxSleepiness);
            if (numbersToBurst == 0) {
                numbersToBurst = numbersBetweenBursts;
                burstInProgress = true;
                D = A;
                x = 0;
                System.out.println("# burst begins: D: " + D);
            }
            if (!burstInProgress) {
                wait = randomGenerator.nextInt(maxWait);
                numbersToBurst--;
            } else {
                double theta = ((B*x)/5) * degreeToRadian;
                double y = D - A * Math.sin(theta);
                wait = ((int)y != 0) ? (int)y : wait;
                /*
                System.out.println("#sbh: x== " + x +
                                   ", y==" + y + 
                                   ", theta (degree)==" + (B*x)/5 +
                                   ", theta (radian)==" + theta +
                                   ", sin(theta): " + Math.sin(theta) +
                                   ", A*sin(theta): " + A*Math.sin(theta));
                */
                x++;
                if (x == width) {
                    burstInProgress = false;
                    System.out.println("# burst ends");
                }
            }
            System.out.println(input + "," + sleepiness + "," + wait);
        }
    } 
}
