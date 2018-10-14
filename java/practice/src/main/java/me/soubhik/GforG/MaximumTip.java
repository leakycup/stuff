package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sb8 on 10/14/18.
 * from https://practice.geeksforgeeks.org/problems/maximum-tip-calculator/0
 */
public class MaximumTip {
    private static class TestCase {
        private int n;
        private int x;
        private int y;
        private int[] a;
        private int[] b;
        TestCase(int n, int x, int y, int[] a, int[] b) {
            this.n = n;
            this.x = x;
            this.y = y;
            this.a = a;
            this.b = b;
        }
    }

    public static int maxTipRecursive(int n, int x, int y, int[] a, int[] b, int[][][] memoizationArray) {
        if (memoizationArray[n][x][y] != 0) {
            return memoizationArray[n][x][y];
        }

        if (x == 0) {
            int tip = 0;
            for (int i=0; i < n; i++) {
                tip += b[i];
            }

            memoizationArray[n][x][y] = tip;
            return tip;
        }

        if (y == 0) {
            int tip = 0;
            for (int i=0; i < n; i++) {
                tip += a[i];
            }

            memoizationArray[n][x][y] = tip;
            return tip;
        }

        if (n == 1) {
            int tip = (a[n-1] > b[n-1]) ? a[n-1] : b[n-1];
            memoizationArray[n][x][y] = tip;
            return tip;
        }

        int tipA = a[n-1] + maxTip(n-1, x-1, y, a, b);
        int tipB = b[n-1] + maxTip(n-1, x, y-1, a, b);

        int tip = (tipA > tipB) ? tipA : tipB;
        memoizationArray[n][x][y] = tip;
        return tip;
    }

    public static int maxTip(int n, int x, int y, int[] a, int[] b) {
        int[][][] memoizationArray = new int[n+1][x+1][y+1];

        int tip = maxTipRecursive(n, x, y, a, b, memoizationArray);

        return tip;
    }

    private static void test1() {
        int n = 4;
        int x = 6;
        int y = 0;
        int[] a = new int[] {2,4,5,2};
        int[] b = new int[] {3,4,8,6};
        int expected = 13;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test2() {
        int n = 4;
        int x = 0;
        int y = 4;
        int[] a = new int[] {3,4,8,6};
        int[] b = new int[] {2,4,5,2};
        int expected = 13;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test3() {
        int n = 4;
        int x = 4;
        int y = 4;
        int[] a = new int[] {3,4,8,6};
        int[] b = new int[] {2,6,5,12};
        int expected = 29;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test4() {
        int n = 6;
        int x = 2;
        int y = 4;
        int[] a = new int[] {8, 12, 9, 7, 15, 6};
        int[] b = new int[] {3, 4, 1, 1, 3, 4};
        int expected = 36;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test5() {
        int n = 6;
        int x = 4;
        int y = 2;
        int[] a = new int[] {3, 4, 1, 1, 3, 4};
        int[] b = new int[] {8, 12, 9, 7, 15, 6};
        int expected = 36;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test6() {
        int n = 6;
        int x = 4;
        int y = 2;
        int[] a = new int[] {3, 14, 10, 1, 3, 4};
        int[] b = new int[] {8, 12, 9, 7, 15, 6};
        int expected = 53;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test7() {
        int n = 5;
        int x = 3;
        int y = 3;
        int[] a = new int[] {1, 2, 3, 4, 5};
        int[] b = new int[] {5, 4, 3, 2, 1};
        int expected = 21;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test8() {
        int n = 8;
        int x = 4;
        int y = 4;
        int[] a = new int[] {1, 4, 3, 2, 7, 5, 9, 6};
        int[] b = new int[] {1, 2, 3, 6, 5, 4, 9, 8};
        int expected = 43;
        int actual = maxTip(n, x, y, a, b);
        assert (expected == actual);
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
        test8();
    }

    public static void main (String[] args)
    {
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
                String[] parts = line.split(" ");
                int n = Integer.parseInt(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);

                int[] a = new int[n];
                line = reader.readLine();
                int j = 0;
                for (String s: line.split(" ")) {
                    s = s.trim();
                    if (s.isEmpty()) {
                        continue;
                    }
                    a[j] = Integer.parseInt(s);
                    j++;
                }

                int[] b = new int[n];
                line = reader.readLine();
                int k = 0;
                for (String s: line.split(" ")) {
                    s = s.trim();
                    if (s.isEmpty()) {
                        continue;
                    }
                    b[k] = Integer.parseInt(s);
                    k++;
                }

                testCases[i] = new TestCase(n, x, y, a, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int tip = maxTip(testCase.n, testCase.x, testCase.y, testCase.a, testCase.b);
            System.out.println(tip);
        }
    }
}
