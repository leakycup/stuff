package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by soubhik on 04-11-2018.
 * from: https://practice.geeksforgeeks.org/problems/how-many-xs/0
 */
public class HowManyX {
    private static List<Integer> toDigits(int n) {
        if (n == 0) {
            return Arrays.asList(0);
        }

        List<Integer> digits = new ArrayList<>();

        int dividend = n;
        int divisor = 10;
        while (dividend != 0) {
            int digit = dividend % divisor;
            digits.add(digit);
            dividend /= divisor;
        }

        return digits;
    }

    // O(m*n) where m is number of numbers in the given range (upper - lower - 1) and
    // n is the average number of digits in numbers in the given range
    public static int howManyXSimple(int lower, int upper, int x) {
        int count = 0;

        for (int i = lower+1; i < upper; i++) {
            List<Integer> digits = toDigits(i);
            for (int digit: digits) {
                if (x == digit) {
                    count++;
                }
            }
        }

        return count;
    }

    private static void test(int lower, int upper, int x, int expected) {
        int actual = howManyXSimple(lower, upper, x);
        assert (expected == actual);
    }

    private static void test1() {
        test(100, 250, 3, 35);
    }

    private static void test2() {
        test(10000, 12345, 2, 1120);
    }

    private static void test3() {
        test(20, 21, 0, 0);
    }

    private static void test4() {
        test(899, 1000, 9, 120);
    }

    private static void test5() {
        test(1100, 1345, 1, 398);
    }

    private static class TestCase {
        int lower;
        int upper;
        int x;

        public TestCase(int lower, int upper, int x) {
            this.lower = lower;
            this.upper = upper;
            this.x = x;
        }
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();

        int numTestCases;
        TestCase[] testCases;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            numTestCases = Integer.parseInt(line);
            testCases = new TestCase[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                line = reader.readLine();
                int x = Integer.parseInt(line);

                line = reader.readLine();
                String[] parts = line.split(" ");
                int lower = Integer.parseInt(parts[0]);
                int upper = Integer.parseInt(parts[1]);
                testCases[i] = new TestCase(lower, upper, x);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int count = howManyXSimple(testCase.lower, testCase.upper, testCase.x);
            System.out.println(count);
        }
    }
}
