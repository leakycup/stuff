package me.soubhik.GforG;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sb8 on 10/29/18.
 * from https://practice.geeksforgeeks.org/problems/rotten-oranges/0
 */
public class RottenOranges {
    public static int findMinTime(int r, int c, int[][] tomatoes) {
        int goodTomatoes = 0;
        List<Pair<Integer, Integer>> rottenTomatoes = new LinkedList<>();
        for (int i=0; i < r; i++) {
            for (int j=0; j < c; j++) {
                if (tomatoes[i][j] == 1) {
                    goodTomatoes++;
                } else if (tomatoes[i][j] == 2) {
                    rottenTomatoes.add(new ImmutablePair<>(i, j));
                }
            }
        }

        int steps = 0;
        while (goodTomatoes > 0) {
            List<Pair<Integer, Integer>> newlyRottenTomatoes = new LinkedList<>();
            for (Pair<Integer, Integer> pair: rottenTomatoes) {
                int i = pair.getLeft();
                int j = pair.getRight();
                if (((i+1) < r) && (tomatoes[i+1][j] == 1)) {
                    newlyRottenTomatoes.add(new ImmutablePair<>(i+1, j));
                    tomatoes[i+1][j] = 2;
                }
                if (((i-1) >= 0) && (tomatoes[i-1][j] == 1)) {
                    newlyRottenTomatoes.add(new ImmutablePair<>(i-1, j));
                    tomatoes[i-1][j] = 2;
                }
                if (((j+1) < c) && (tomatoes[i][j+1] == 1)) {
                    newlyRottenTomatoes.add(new ImmutablePair<>(i, j+1));
                    tomatoes[i][j+1] = 2;
                }
                if (((j-1) >= 0) && (tomatoes[i][j-1] == 1)) {
                    newlyRottenTomatoes.add(new ImmutablePair<>(i, j-1));
                    tomatoes[i][j-1] = 2;
                }
            }
            if (newlyRottenTomatoes.isEmpty()) {
                return -1;
            }
            steps++;
            goodTomatoes -= newlyRottenTomatoes.size();
            rottenTomatoes.addAll(newlyRottenTomatoes);
        }

        return steps;
    }

    private static void test1() {
        int[][] oranges = new int[][] {{2, 1, 0, 2, 1}, {1, 0, 1, 2, 1}, {1, 0, 0, 2, 1}};
        int r = 3;
        int c = 5;
        int expected = 2;
        int actual = findMinTime(r, c, oranges);

        assert (expected == actual);
    }

    private static void test2() {
        int[][] oranges = new int[][] {{2, 1, 0, 2, 1}, {0, 0, 1, 2, 1}, {1, 0, 0, 2, 1}};
        int r = 3;
        int c = 5;
        int expected = -1;
        int actual = findMinTime(r, c, oranges);

        assert (expected == actual);
    }

    private static void test() {
        test1();
        test2();
    }

    public static class TestCase {
        int r;
        int c;
        int[][] oranges;

        public TestCase(int r, int c, int[][] oranges) {
            this.r = r;
            this.c = c;
            this.oranges = oranges;
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
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);

                line = reader.readLine();
                parts = line.split(" ");
                int[][] oranges = new int[r][c];
                int l = 0;
                for (int j=0; j < r; j++) {
                    for (int k=0; k < c; k++) {
                        oranges[j][k] = Integer.parseInt(parts[l]);
                        l++;
                    }
                }
                testCases[i] = new TestCase(r, c, oranges);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int steps = findMinTime(testCase.r, testCase.c, testCase.oranges);
            System.out.println(steps);
        }
    }
}
