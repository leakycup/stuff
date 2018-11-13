package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by soubhik on 13-11-2018.
 * from https://practice.geeksforgeeks.org/problems/rotate-a-2d-array-without-using-extra-space/0
 */
public class Rotate2DArray {
    private static class Coordinate {
        private final int i;
        private final int j;

        public Coordinate(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static class Side {
        private final  Coordinate start;
        private final  Coordinate end;

        public Side(int i1, int j1, int i2, int j2) {
            this(new Coordinate(i1, j1), new Coordinate(i2, j2));
        }

        public Side(Coordinate start, Coordinate end) {
            this.start = start;
            this.end = end;
        }

        public boolean contains(Coordinate q) {
            return (((q.i >= start.i) && (q.i <= end.i)) && ((q.j >= start.j) && (q.j <= end.j)));
        }
    }

    private static class Square {
        private final int length;
        private final Side up;
        private final Side down;
        private final Side left;
        private final Side right;

        public Square(int origin, int length) {
            this.length = length;

            this.up = new Side(origin, origin, origin, origin+length-1);
            this.down = new Side(origin+length-1, origin, origin+length-1, origin+length-1);
            this.left = new Side(origin, origin, origin+length-1, origin);
            this.right = new Side(origin, origin+length-1, origin+length-1, origin+length-1);
        }

        public Coordinate rotateClockwise(Coordinate q) {
            int i, j;
            if (up.contains(q)) {
                j = right.start.j;
                i = q.i + (length - 1) - (j - q.j);
            } else if (right.contains(q)) {
                i = down.start.i;
                j = q.j - (length - 1) + (i - q.i);
            } else if (down.contains(q)) {
                j = left.start.j;
                i = q.i - (length - 1) + (q.j - j);
            } else if (left.contains(q)) {
                i = up.start.i;
                j = q.j + (length - 1) - (q.i - i);
            } else {
                throw new IllegalArgumentException("the point is not on any of the sides of the square");
            }

            return new Coordinate(i, j);
        }
    }

    private static int moveData(int[][] matrix, Coordinate source, Coordinate destination) {
        int temp = matrix[destination.i][destination.j];
        matrix[destination.i][destination.j] = matrix[source.i][source.j];

        return temp;
    }

    private static int moveData(int[][] matrix, int data, Coordinate destination) {
        int temp = matrix[destination.i][destination.j];
        matrix[destination.i][destination.j] = data;

        return temp;
    }

    private static void rotateClockwise(int[][] matrix, Coordinate q, Square square) {
        Coordinate r = square.rotateClockwise(q);
        Coordinate s = square.rotateClockwise(r);
        Coordinate t = square.rotateClockwise(s);

        int temp = moveData(matrix, q, r);
        temp = moveData(matrix, temp, s);
        temp = moveData(matrix, temp, t);
        moveData(matrix, temp, q);
    }

    public static void rotateClockwise(int[][] matrix) {
        int origin = 0;
        int length = matrix.length;

        while (length > 1) {
            Square square = new Square(origin, length);
            for (int i = 0; i < length - 1; i++) {
                rotateClockwise(matrix, new Coordinate(origin, origin+i), square);
            }
            origin++;
            length -= 2;
        }
    }

    private static void test1() {
        int[][] matrix = new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] expected = new int[][] {{7, 4, 1}, {8, 5, 2}, {9, 6, 3}};
        rotateClockwise(matrix);

        assert (expected.length == matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            assert (Arrays.equals(expected[i], matrix[i]));
        }
    }

    private static void test2() {
        int[][] matrix = new int[][] {{56, 96}, {91, 54}};
        int[][] expected = new int[][] {{91, 56}, {54, 96}};
        rotateClockwise(matrix);

        assert (expected.length == matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            assert (Arrays.equals(expected[i], matrix[i]));
        }
    }

    private static void test3() {
        int[][] matrix = new int[][] {};
        int[][] expected = new int[][] {};
        rotateClockwise(matrix);

        assert (expected.length == matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            assert (Arrays.equals(expected[i], matrix[i]));
        }
    }

    private static void test4() {
        int[][] matrix = new int[][] {{4}};
        int[][] expected = new int[][] {{4}};
        rotateClockwise(matrix);

        assert (expected.length == matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            assert (Arrays.equals(expected[i], matrix[i]));
        }
    }

    private static void test5() {
        int[][] matrix = new int[][] {{8, 4, 18, 22, 6}, {7,3, 17, 21, 5}, {9, 5,  19, 23, 7}, {6, 2, 16, 20, 4}, {1, 2, 3, 4, 5}};
        int[][] expected = new int[][] {{1, 6, 9, 7, 8}, {2, 2, 5, 3, 4}, {3, 16, 19, 17, 18}, {4, 20, 23, 21, 22}, {5, 4, 7, 5, 6}};
        rotateClockwise(matrix);

        assert (expected.length == matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            assert (Arrays.equals(expected[i], matrix[i]));
        }
    }

    private static void test6() {
        int[][] matrix = new int[][] {{8, 4, 18, 22}, {7,3, 17, 21}, {9, 5,  19, 23}, {6, 2, 16, 20}};
        int[][] expected = new int[][] {{6, 9, 7, 8}, {2, 5, 3, 4}, {16, 19, 17, 18}, {20, 23, 21, 22}};
        rotateClockwise(matrix);

        assert (expected.length == matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            assert (Arrays.equals(expected[i], matrix[i]));
        }
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
    }

    private static class TestCase {
        int[][] matrix;

        public TestCase(int[][] matrix) {
            this.matrix = matrix;
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

                int[][] matrix = new int[n][n];
                line = reader.readLine();
                String[] parts = line.split(" ");
                int l = 0;
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        matrix[j][k] = Integer.parseInt(parts[l]);
                        l++;
                    }
                }

                testCases[i] = new TestCase(matrix);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            rotateClockwise(testCase.matrix);
            for (int i = 0; i < testCase.matrix.length; i++) {
                for (int j = 0; j < testCase.matrix[i].length; j++) {
                    System.out.print(testCase.matrix[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
}
