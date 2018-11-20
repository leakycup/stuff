package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by soubhik on 20-11-2018.
 */
public class PaintersPartition {
    // heights.length > 0, k > 0
    public static int minTime(int[] heights, int k) {
        int[][] memo = new int[heights.length][k]; //0-initialized by Java
        memo[0][0] = sum(heights, 0, heights.length); //O(n)
        for (int i = 1; i < memo.length; i++) { //O(n)
            memo[i][0] = memo[i-1][0] - heights[i-1];
        }

        return minTimeRecursive(heights, 0, k-1, memo); //O(n*k)
    }

    private static int sum(int[] heights, int i, int l) {
        int sum =0;
        for (int j = i; j < l; j++) {
            sum += heights[j];
        }

        return sum;
    }

    // k == number of workers - 1
    private static int minTimeRecursive(int[] heights, int i, int k, int[][] memo) {
        if (memo[i][k] != 0) {
            return memo[i][k];
        }

        if ((heights.length - i) == 1) {
            memo[i][k] = heights[heights.length - 1];
            return memo[i][k];
        }

        int workDone = 0;
        int minTimeTaken = Integer.MAX_VALUE;
        for (int j = i; j < heights.length; j++) {
            int remainingTime = minTimeRecursive(heights, j, k-1, memo);
            int timeTaken = (workDone > remainingTime) ? workDone : remainingTime;
            if (minTimeTaken > timeTaken) {
                minTimeTaken = timeTaken;
            }
            workDone += heights[j];
        }

        if (workDone < minTimeTaken) {
            minTimeTaken = workDone;
        }


        memo[i][k] = minTimeTaken;
        return memo[i][k];
    }

    private static void test1() {
        int[] heights = new int[] {2, 4, 3, 10};
        int k = 2;
        int expected = 10;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test2() {
        int[] heights = new int[] {2, 4, 3, 10, 1};
        int k = 2;
        int expected = 11;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test3() {
        int[] heights = new int[] {2, 1, 1, 10, 5};
        int k = 2;
        int expected = 14;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test4() {
        int[] heights = new int[] {2, 1, 1, 10, 5};
        int k = 1;
        int expected = 19;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test5() {
        int[] heights = new int[] {6};
        int k = 1;
        int expected = 6;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test6() {
        int[] heights = new int[] {2, 1, 1, 10, 5};
        int k = 3;
        int expected = 10;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test7() {
        int[] heights = new int[] {2, 8, 3, 5, 4, 6};
        int k = 3;
        int expected = 10;
        int actual = minTime(heights, k);

        assert (expected == actual);
    }

    private static void test8() {
        int[] heights = new int[] {2, 200, 3, 5, 4, 6};
        int k = 3;
        int expected = 200;
        int actual = minTime(heights, k);

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

    private static class TestCase {
        private int k;
        private int[] heights;

        public TestCase(int[] heights, int k) {
            this.heights = heights;
            this.k = k;
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
                String[] parts = line.split(" ");
                int k = Integer.parseInt(parts[0]);
                int n = Integer.parseInt(parts[1]);

                int[] heights = new int[n];
                line = reader.readLine();
                parts = line.split(" ");
                for (int j = 0; j < n; j++) {
                    heights[j] = Integer.parseInt(parts[j]);
                }

                testCases[i] = new TestCase(heights, k);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int minTime = minTime(testCase.heights, testCase.k);
            System.out.println(minTime);
        }
    }
}
