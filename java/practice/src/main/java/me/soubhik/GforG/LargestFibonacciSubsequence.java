package me.soubhik.GforG;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by soubhik on 30-11-2018.
 * from https://practice.geeksforgeeks.org/problems/largest-fibonacci-subsequence/0
 */
public class LargestFibonacciSubsequence {
    private static Pair<Integer, Integer> findMinMax(int[] input) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i: input) {
            if (i > max) {
                max = i;
            }
            if (i < min) {
                min = i;
            }
        }

        return new ImmutablePair<>(min, max);
    }

    public static List<Integer> findLargestFibonacciSubsequence(int[] input) {
        Pair<Integer, Integer> minMax = findMinMax(input);
        int min = minMax.getLeft();
        int max = minMax.getRight();

        Set<Integer> fibonacciNumbers = new HashSet<>();
        int f1 = 0;
        int f2 = 1;
        while (f1 < min) {
            int f = f1 + f2;
            f1 = f2;
            f2 = f;
        }

        fibonacciNumbers.add(f1);

        while (f2 <= max) {
            fibonacciNumbers.add(f2);
            int f = f1 + f2;
            f1 = f2;
            f2 = f;
        }

        List<Integer> output = new ArrayList<>();
        for (int i: input) {
            if (fibonacciNumbers.contains(i)) {
                output.add(i);
            }
        }

        return output;
    }

    private static void test1() {
        int[] input = new int[] {6, 2, 9, 8};
        List<Integer> expected = Arrays.asList(2, 8);
        List<Integer> actual = findLargestFibonacciSubsequence(input);

        assert (expected.equals(actual));
    }

    private static void test2() {
        int[] input = new int[] {6, 8, 9, 2};
        List<Integer> expected = Arrays.asList(8, 2);
        List<Integer> actual = findLargestFibonacciSubsequence(input);

        assert (expected.equals(actual));
    }

    private static void test3() {
        int[] input = new int[] {6, 2, 9, 2, 8};
        List<Integer> expected = Arrays.asList(2, 2, 8);
        List<Integer> actual = findLargestFibonacciSubsequence(input);

        assert (expected.equals(actual));
    }

    private static void test4() {
        int[] input = new int[] {6, 2, 9, 2, 8, 21, 6};
        List<Integer> expected = Arrays.asList(2, 2, 8, 21);
        List<Integer> actual = findLargestFibonacciSubsequence(input);

        assert (expected.equals(actual));
    }

    private static void test5() {
        int[] input = new int[] {5, 4, 13, 9, 6};
        List<Integer> expected = Arrays.asList(5, 13);
        List<Integer> actual = findLargestFibonacciSubsequence(input);

        assert (expected.equals(actual));
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
    }

    private static class TestCase {
        int[] input;
        TestCase(int[] input) {
            this.input = input;
        }
    }

    public static void main(String[] args) {
        test();

        int numTestCases;
        TestCase[] testCases;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            numTestCases = Integer.parseInt(line);
            testCases = new TestCase[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                line = reader.readLine();
                int n = Integer.parseInt(line);
                int[] input = new int[n];

                line = reader.readLine();
                String[] parts = line.split(" ");
                for (int j = 0; j < n; j++) {
                    input[j] = Integer.parseInt(parts[j]);
                }

                testCases[i] = new TestCase(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            List<Integer> subsequence = findLargestFibonacciSubsequence(testCase.input);
            for (Integer i: subsequence) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
}
