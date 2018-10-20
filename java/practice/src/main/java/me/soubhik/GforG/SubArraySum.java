package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by sb8 on 10/20/18.
 */
public class SubArraySum {
    private static class TestCase {
        private int[] array;
        private int sum;
        private int numberOfElements;

        public TestCase(int[] array, int sum, int numberOfElements) {
            this.array = array;
            this.sum = sum;
            this.numberOfElements = numberOfElements;
        }

        public TestCase(int[] array, int sum) {
            this(array, sum, 4);
        }
    }

    private static class Solution {
        private int[][][] memoizationArray;

        public int[] findTargetSum(int[] array, int sum, int numberOfElements) {
            int[] indices = new int[numberOfElements];
            Arrays.fill(indices, -1);

            this.memoizationArray = new int[array.length][sum+1][numberOfElements+1];

            findTargetSum(array, indices, 0, 0, sum, numberOfElements);
            if (indices[0] < 0) {
                indices = null;
            }

            return indices;
        }

        private int subArraySum(int[] array, int start) {
            int sum = 0;
            for (int i = start; i < array.length; i++) {
                sum += array[i];
            }

            return sum;
        }

        private void findTargetSum(int[] array, int[] indices, int arrayIndex, int indicesIndex, int sum, int numberOfElements) {
            if (memoizationArray[arrayIndex][sum][numberOfElements] < 0) {
                return;
            }

            findTargetSumRecursive(array, indices, arrayIndex, indicesIndex, sum, numberOfElements);
            if (indices[indicesIndex] < 0) {
                memoizationArray[arrayIndex][sum][numberOfElements] = -1;
            }
        }

        private void findTargetSumRecursive(int[] array, int[] indices, int arrayIndex, int indicesIndex, int sum, int numberOfElements) {
            int remainingElements = array.length - arrayIndex;

            if (remainingElements == 0) {
                return;
            }

            if (remainingElements < numberOfElements) {
                return;
            }

            if (remainingElements == numberOfElements) {
                int actualSum = subArraySum(array, arrayIndex);
                if (sum == actualSum) {
                    for (int i = indicesIndex; i < indices.length; i++) {
                        indices[i] = arrayIndex;
                        arrayIndex++;
                    }
                }

                return;
            }

            if (numberOfElements == 1) {
                if (sum == array[arrayIndex]) {
                    indices[indicesIndex] = arrayIndex;
                    return;
                }
            } else if (array[arrayIndex] <= sum) {
                findTargetSum(array, indices, arrayIndex+1, indicesIndex+1, sum-array[arrayIndex], numberOfElements-1);
                if (indices[indicesIndex+1] >= 0) {
                    indices[indicesIndex] = arrayIndex;
                    return;
                }
            }

            findTargetSum(array, indices, arrayIndex+1, indicesIndex, sum, numberOfElements);
        }
    }

    private static void test1() {
        int[] array = new int[] {1, 5, 1, 0, 6, 0};
        int sum = 7;
        int numberOfElements = 4;
        Solution solution = new Solution();
        int[] indices = solution.findTargetSum(array, sum, numberOfElements);

        assert (indices != null);
        assert (indices.length == numberOfElements);
        assert (indices[0] == 0);
        assert (indices[1] == 1);
        assert (indices[2] == 2);
        assert (indices[3] == 3);
    }

    private static void test2() {
        int[] array = new int[] {1, 5, 1, 0, 6, 0};
        int sum = 7;
        int numberOfElements = 2;
        Solution solution = new Solution();
        int[] indices = solution.findTargetSum(array, sum, numberOfElements);

        assert (indices != null);
        assert (indices.length == numberOfElements);
        assert (indices[0] == 0);
        assert (indices[1] == 4);
    }

    private static void test3() {
        int[] array = new int[] {1, 5, 1, 0, 6, 0};
        int sum = 7;
        int numberOfElements = 3;
        Solution solution = new Solution();
        int[] indices = solution.findTargetSum(array, sum, numberOfElements);

        assert (indices != null);
        assert (indices.length == numberOfElements);
        assert (indices[0] == 0);
        assert (indices[1] == 1);
        assert (indices[2] == 2);
    }

    private static void test4() {
        int[] array = new int[] {1, 5, 1, 0, 6, 0};
        int sum = 7;
        int numberOfElements = 5;
        Solution solution = new Solution();
        int[] indices = solution.findTargetSum(array, sum, numberOfElements);

        assert (indices != null);
        assert (indices.length == numberOfElements);
        assert (indices[0] == 0);
        assert (indices[1] == 1);
        assert (indices[2] == 2);
        assert (indices[3] == 3);
        assert (indices[4] == 5);
    }

    private static void test5() {
        int[] array = new int[] {1, 5, 1, 0, 6, 0};
        int sum = 7;
        int numberOfElements = 6;
        Solution solution = new Solution();
        int[] indices = solution.findTargetSum(array, sum, numberOfElements);

        assert (indices == null);
    }

    private static void test6() {
        int[] array = new int[] {1, 5, 1, 0, 6, 0};
        int sum = 7;
        int numberOfElements = 1;
        Solution solution = new Solution();
        int[] indices = solution.findTargetSum(array, sum, numberOfElements);

        assert (indices == null);
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
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
                int arraySize = Integer.parseInt(line);
                int[] array = new int[arraySize];

                line = reader.readLine();
                String[] parts = line.split(" ");
                int j = 0;
                for (String part: parts) {
                    part = part.trim();
                    if (part.isEmpty()) {
                        continue;
                    }
                    array[j] = Integer.parseInt(part);
                    j++;
                }

                line = reader.readLine();
                int sum = Integer.parseInt(line);

                testCases[i] = new TestCase(array, sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            Solution solution = new Solution();
            int[] indices = solution.findTargetSum(testCase.array, testCase.sum, testCase.numberOfElements);
            if (indices == null) {
                System.out.println("0");
            } else {
                System.out.println("1");
            }
        }
    }
}
