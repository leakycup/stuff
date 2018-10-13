package me.soubhik.GforG;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by sb8 on 10/13/18.
 * from https://practice.geeksforgeeks.org/problems/find-smallest-range-containing-elements-from-k-lists/1
 */
public class FindSmallestRange {
    private static class Problem {
        int[][] arrays;
        int n;
        int k;
        int start;
        int end;
        Integer[] mergedArrays;

        public Problem(int[][] arrays, Integer[] mergedArrays, int n, int k, int start, int end) {
            this.arrays = arrays;
            this.mergedArrays = mergedArrays;
            this.n = n;
            this.k = k;
            this.start = start;
            this.end = end;
        }
    }

    private static int findSmallestIndex(int[] indices) {
        int smallest = Integer.MAX_VALUE;
        for (int index: indices) {
            smallest = (index < smallest) ? index : smallest;
        }

        return smallest;
    }

    private static int findArrayWithSmallestValue(int[][] arrays, int[] indices) {
        int smallest = Integer.MAX_VALUE;
        int smallestArray = -1;
        for (int i=0; i < indices.length; i++) {
            int index = indices[i];
            if (index >= arrays[i].length) {
                continue;
            }
            int value = arrays[i][index];
            if (value < smallest) {
                smallest = value;
                smallestArray = i;
            }
        }

        return smallestArray;
    }

    private static Triple<Integer[], Integer, Integer> findSmallestRange(int[][] arrays, int n, int k) {
        Integer[] mergedArrays = new Integer[n*k];
        int[] sourceIndices = new int[k];
        int[] destinationIndices = new int[k];
        int[] startIndices = new int[n*k];
        int mask = (1 << k) - 1; //a k-bit mask to track the arrays not visited so far. works only if k <= 32.

        Arrays.fill(sourceIndices, 0);
        Arrays.fill(startIndices, -1);

        for (int i=0; i < mergedArrays.length; i++) {
            int array = findArrayWithSmallestValue(arrays, sourceIndices);
            mergedArrays[i] = arrays[array][sourceIndices[array]];
            destinationIndices[array] = i;
            sourceIndices[array]++;
            //we just visited an array. lets clear the corresponding bit of the mask
            mask &= ~(1 << array);
            if (mask == 0) {
                startIndices[i] = findSmallestIndex(destinationIndices);
            }
        }

        //find smallest range
        int smallestLength = Integer.MAX_VALUE;
        int rangeStart = -1;
        int rangeEnd = -1;
        for (int i=0; i < startIndices.length; i++) {
            int start = startIndices[i];
            if (start < 0) {
                continue;
            }
            int length = mergedArrays[i] - mergedArrays[start];
            if (length < smallestLength) {
                smallestLength = length;
                rangeStart = mergedArrays[start];
                rangeEnd = mergedArrays[i];
            }
        }
        System.out.println(rangeStart + " " + rangeEnd);

        return new ImmutableTriple<Integer[], Integer, Integer>(mergedArrays, rangeStart, rangeEnd);
    }

    private static void findSmallestRange(int arrays[][], Integer[] mergedArray, int n, int k, int start, int end) {
        Triple<Integer[], Integer, Integer> triple = findSmallestRange(arrays, n, k);
        assert Arrays.equals(mergedArray, triple.getLeft());
        assert (triple.getMiddle() == start);
        assert (triple.getRight() == end);
    }

    public static void main(String args[]) {
        /*
         * test data
==================================
2
5 3
1 3 5 7 9 0 2 4 6 8 2 3 5 7 11
1 2
4 3
1 2 3 4 5 6 7 8 9 10 11 12
4 9
==================================
1
6 5
3 7 22 36 56 80 6 20 31 31 36 96 1 2 6 8 42 69 2 8 46 50 86 89 0 2 9 12 63 71
6 9
==================================
1
6 5
3 7 22 36 56 80 20 31 31 36 60 96 1 2 6 8 42 69 2 8 46 50 86 89 0 2 9 12 63 71
7 20
==================================
1
6 5
3 7 22 36 56 80 31 31 36 60 96 200 1 2 6 8 42 69 2 8 46 50 86 89 0 2 9 12 63 71
50 69
==================================
1
6 5
3 7 22 36 56 80 31 31 36 60 96 200 1 2 6 8 42 69 2 8 46 86 89 500 0 2 9 12 63 71
42 63
==================================
1
6 5
3 7 22 36 56 80 31 31 36 60 96 200 1 2 6 8 69 4200 2 8 46 86 89 500 0 2 9 12 63 71
8 31
==================================
         */
        Scanner in = new Scanner(System.in);
        int testCases = in.nextInt();
        Problem[] problems = new Problem[testCases];
        for (int i = 0; i < testCases; i++) {
            int n = in.nextInt();
            int k = in.nextInt();
            int[][] input = new int[k][n];
            Integer[] mergedInput = new Integer[n*k];
            for (int j=0; j<n*k; j++) {
                input[j/n][j%n] = in.nextInt();
                mergedInput[j] = input[j/n][j%n];
            }
            int start = in.nextInt();
            int end = in.nextInt();
            Arrays.sort(mergedInput);
            problems[i] = new Problem(input, mergedInput, n, k, start, end);
        }

        for (Problem problem: problems) {
            findSmallestRange(problem.arrays, problem.mergedArrays, problem.n, problem.k, problem.start, problem.end);
        }
    }
}
