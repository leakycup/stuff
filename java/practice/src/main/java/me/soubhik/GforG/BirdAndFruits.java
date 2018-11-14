package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by soubhik on 14-11-2018.
 * from https://practice.geeksforgeeks.org/problems/bird-and-maximum-fruit-gathering/0
 */
public class BirdAndFruits {
    private static final int MOD = 1000000000 + 7;

    public static int maxFruits(int[] trees, int n, int m) {
        for (int i = 1; i < n; i++) {
            trees[i] %= MOD;
        }

        int window = m;
        if (window > n) {
            window = n;
        }

        int maxValue = 0;
        for (int i = 0; i < window; i++) {
            maxValue += trees[i];
            maxValue %= MOD;
        }

        int previousValue = maxValue;
        for (int i = 1; i < n; i++) {
            int newValue = previousValue;
            newValue -= trees[i-1];
            newValue += trees[(i+window-1)%n];
            newValue %= MOD;

            if (newValue > maxValue) {
                maxValue = newValue;
            }

            previousValue = newValue;
        }

        return maxValue;
    }

    private static void test1() {
        int[] trees = new int[] {2, 1, 3, 5, 0, 1, 4};
        int n = trees.length;
        int m = 3;

        int expected = 9;
        int actual = maxFruits(trees, n, m);

        assert (expected == actual);
    }

    private static void test2() {
        int[] trees = new int[] {1, 6, 2, 5, 3, 4};
        int n = trees.length;
        int m = 2;

        int expected = 8;
        int actual = maxFruits(trees, n, m);

        assert (expected == actual);
    }

    private static void test() {
        test1();
        test2();
    }

    private static class TestCase {
        int[] trees;
        int n;
        int m;

        public TestCase(int[] trees, int n, int m) {
            this.trees = trees;
            this.n = n;
            this.m = m;
        }
    }

    public static void main(String[] args) {
        int numTestCases;
        TestCase[] testCases;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            numTestCases = Integer.parseInt(line);
            testCases = new TestCase[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                line = reader.readLine();
                String[] parts = line.split(" ");
                int n = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int[] trees = new int[n];

                line = reader.readLine();
                parts = line.split(" ");
                for (int j = 0; j < n; j++) {
                    trees[j] = Integer.parseInt(parts[j]);
                }

                testCases[i] = new TestCase(trees, n, m);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for  (TestCase testCase:  testCases) {
            int value = maxFruits(testCase.trees, testCase.n, testCase.m);
            System.out.println(value);
        }
    }
}
