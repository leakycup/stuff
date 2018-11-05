package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by soubhik on 05-11-2018.
 * from https://practice.geeksforgeeks.org/problems/largest-sum-subarray-of-size-at-least-k/0
 */
public class LargestSumSubarray {
    private static PriorityQueue<Integer> heapify(int[] elements) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Integer first = (Integer)o1;
                Integer second = (Integer)o2;

                return second.compareTo(first);
            }
        });

        for (Integer element: elements) {
            maxHeap.add(element);
        }

        return maxHeap;
    }

    //O(n*log(n))
    private static int findMaxSubsetSum(int[] elements, int k) {
        int sum = 0;
        int numElements = 0;

        //O(n*log(n))
        PriorityQueue<Integer> maxHeap = heapify(elements);
        //O(n*log(n))
        while (!maxHeap.isEmpty()) {
            int element = maxHeap.remove();
            if (numElements < k) {
                sum += element;
                numElements++;
            } else if (element > 0) {
                sum += element;
            } else {
                break;
            }
        }

        return sum;
    }

    private static int findMaxSubarraySum(int[] elements, int k) {
        //subarray sums for all subarrays starting at a given index of elements[]
        int[][] sumArray = new int[elements.length - k + 1][];
        for (int i = 0; i < sumArray.length; i++) {
            sumArray[i] = new int[elements.length - i - k + 1];
        }

        int sumFirstK = 0;
        for (int i = 0; i < k; i++) {
            sumFirstK += elements[i];
        }
        int maxSum = sumFirstK;

        //sum of subarray of length k starting at index 0
        sumArray[0][0] = sumFirstK;
        //sum of subarrays of length (k+i) starting at index 0
        for (int i = 1; i < sumArray[0].length; i++) {
            // sum of subarray of length k+i starting at elements[0] ==
            // (sum of subarray of length k+i-1 starting at elements[0]) + elements[k+i-1]
            sumArray[0][i] = sumArray[0][i-1] + elements[k + i - 1];
            maxSum = (sumArray[0][i] > maxSum) ? sumArray[0][i] : maxSum;
        }

        //sum of all other subarrays
        for (int i = 1; i < sumArray.length; i++) {
            // sum of subarray of length k starting at elements[i] ==
            // (sum of subarray of length k+1 starting at elements[i-1]) - elements[i-1]
            sumArray[i][0] = sumArray[i-1][1] - elements[i-1];
            maxSum = (sumArray[i][0] > maxSum) ? sumArray[i][0] : maxSum;
            for (int j = 1; j < sumArray[i].length; j++) {
                sumArray[i][j] = sumArray[i][j-1] + elements[i + k + j - 1];
                maxSum = (sumArray[i][j] > maxSum) ? sumArray[i][j] : maxSum;
            }
        }

        return maxSum;
    }

    private static void test1() {
        int[] elements = {1, 2, 3};
        int k = 1;

        int expected = 6;
        int actual = findMaxSubsetSum(elements, k);
        assert (expected == actual);

        expected = 6;
        actual = findMaxSubarraySum(elements, k);
        assert (expected == actual);
    }

    private static void test2() {
        int[] elements = {-4, -2, 1, -3};
        int k = 2;

        int expected = -1;
        int actual = findMaxSubsetSum(elements, k);
        assert (expected == actual);

        expected = -1;
        actual = findMaxSubarraySum(elements, k);
        assert (expected == actual);
    }

    private static void test3() {
        int[] elements = {1, 1, 1, 1, 1, 1};
        int k = 2;

        int expected = 6;
        int actual = findMaxSubsetSum(elements, k);
        assert (expected == actual);

        expected = 6;
        actual = findMaxSubarraySum(elements, k);
        assert (expected == actual);
    }

    private static void test4() {
        int[] elements = {-1, 2, -3, 4};
        int k = 3;

        int expected = 5;
        int actual = findMaxSubsetSum(elements, k);
        assert (expected == actual);

        expected = 3;
        actual = findMaxSubarraySum(elements, k);
        assert (expected == actual);
    }

    private static void test5() {
        int[] elements = {10, -10};
        int k = 1;

        int expected = 10;
        int actual = findMaxSubsetSum(elements, k);
        assert (expected == actual);

        expected = 10;
        actual = findMaxSubarraySum(elements, k);
        assert (expected == actual);
    }

    private static void test6() {
        int[] elements = {5, 7, -9, 3, -4, 2, 1, -8, 9, 10};
        int k = 5;

        int expected = 37;
        int actual = findMaxSubsetSum(elements, k);
        assert (expected == actual);

        expected = 16;
        actual = findMaxSubarraySum(elements, k);
        assert (expected == actual);
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
        int[] elements;
        int k;

        public TestCase(int[] elements, int k) {
            this.elements = elements;
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
                int numElements = Integer.parseInt(line);

                line = reader.readLine();
                String[] parts = line.split(" ");
                int[] elements = new int[numElements];
                int j = 0;
                for (String part: parts) {
                    elements[j] = Integer.parseInt(part);
                    j++;
                }

                line = reader.readLine();
                int k = Integer.parseInt(line);

                testCases[i] = new TestCase(elements, k);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int sum = findMaxSubarraySum(testCase.elements, testCase.k);
            System.out.println(sum);
        }
    }
}

