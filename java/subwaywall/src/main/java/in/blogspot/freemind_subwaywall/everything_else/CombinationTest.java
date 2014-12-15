package in.blogspot.freemind_subwaywall.everything_else;

import java.lang.reflect.Array;

import java.util.List;
import java.util.LinkedList;

class CombinationTest {
    public static void main(String[] args) {
        int maxCombinationLength =  Integer.parseInt(args[0]);
        String[] balls = args[1].split(", *");

        System.out.println("Maximum Number of balls to be drawn: " +
                           maxCombinationLength + ", from:");
        for (String ball: balls) {
            System.out.println(ball);
        }

        List<String> combination = new LinkedList<String>();
        int i = 0;
        int r = maxCombinationLength;

        System.out.println("Combinations:");
        System.out.println("=============");
        int numberOfCombinations = makeCombinatons(balls, combination, i, r);
        System.out.println("total number of combinations of upto " +
                           maxCombinationLength +
                           " balls, drawn with replacement, from " +
                           Array.getLength(balls) + " balls, " +
                           "when the order does not matter, is " +
                           numberOfCombinations);
    }

    private static int makeCombinatons(String[] balls, List<String> combination,
                                       int i, int r) {
        int count = 0;
        int length = combination.size();
        if (r == 1) {
            for (int j = i; j < Array.getLength(balls); j++) {
                combination.add(balls[j]);
                printCombination(combination);
                count++;
                combination.remove(length);
            }
            printCombination(combination);
            count++;
        } else {
            for (int j = i; j < Array.getLength(balls); j++) {
                combination.add(balls[j]);
                count += makeCombinatons(balls, combination, j, (r-1));
                combination.remove(length);
            }
            printCombination(combination);
            count++;
        }

        return (count);
    }

    private static void printCombination(List<String> combination) {
        for (String ball: combination) {
            System.out.printf("%s ", ball);
        }
        System.out.println();
    }
}
