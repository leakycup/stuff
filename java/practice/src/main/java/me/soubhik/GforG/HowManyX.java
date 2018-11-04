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

    private static int fromDigits(List<Integer> digits) {
        int multiplier = 1;
        int number = 0;
        for (int digit: digits) {
            number += digit*multiplier;
            multiplier *= 10;
        }

        return number;
    }

    // find smallest integer in range [lower, upper] (both bounds inclusive) that has x in position-th digit.
    // upper > lower > 0, x in [0, 9], position is a non-negative integer representing (10^position)-th place in a number.
    private static int findSmallestContainingX(int lower, int upper,
                                               List<Integer> lowerDigits, List<Integer> upperDigits,
                                               int x, int position) {
        if((position >= upperDigits.size()) || (position < 0)) {
            return -1;
        }

        // 0 can't be the most significant digit
        if ((position == (upperDigits.size() - 1)) && (x == 0)) {
            return -1;
        }


        List<Integer> smallestDigits = new ArrayList<>(lowerDigits);
        if ((position < smallestDigits.size()) && (smallestDigits.get(position) == x)) {
            return lower;
        }

        //left pad with 0 if necessary (position >= smallestDigits.size())
        for (int i = smallestDigits.size(); i <= position; i++) {
            smallestDigits.add(0);
        }

        smallestDigits.set(position, x);
        for (int i = 0; i < position; i++) {
            smallestDigits.set(i, 0);
        }
        int candidate = fromDigits(smallestDigits);
        if (candidate < lower) {
            int carry = (int)Math.pow(10, position+1);
            candidate += carry;
        }

        if (candidate > upper) {
            return -1;
        }

        return candidate;
    }

    // find largest integer in range [lower, upper] (both bounds inclusive) that has x in position-th digit.
    // upper > lower > 0, x in [0, 9], position is a non-negative integer representing (10^position)-th place in a number.
    private static int findLargestContainingX(int lower, int upper,
                                               List<Integer> lowerDigits, List<Integer> upperDigits,
                                               int x, int position) {
        if((position >= upperDigits.size()) || (position < 0)) {
            return -1;
        }

        // 0 can't be the most significant digit
        if ((position == (upperDigits.size() - 1)) && (x == 0)) {
            return -1;
        }

        List<Integer> largestDigits = new ArrayList<>(upperDigits);
        if (largestDigits.get(position) == x) {
            return upper;
        }

        largestDigits.set(position, x);
        for (int i = 0; i < position; i++) {
            largestDigits.set(i, 9);
        }
        int candidate = fromDigits(largestDigits);
        if (candidate > upper) {
            int borrow = (int)Math.pow(10, position+1);
            candidate -= borrow;
        }

        if (candidate < lower) {
            return -1;
        }

        return candidate;
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

    // O(k) where k is number of digits in upper.
    public static int howManyXFast(int lower, int upper, int x) {
        int count = 0;

        //make bounds inclusive
        lower++;
        upper--;

        List<Integer> lowerDigits = toDigits(lower);
        List<Integer> upperDigits = toDigits(upper);
        int place = 1;
        int divisor = 10;
        //for each position, determine the number of times the digit x appears in the given range
        for (int i = 0; i < upperDigits.size(); i++) {
            int smallest = findSmallestContainingX(lower, upper, lowerDigits, upperDigits, x, i);
            if (smallest < 0) {
                //no number in the given range contains digit x in the position i.
                place *= 10;
                divisor *= 10;
                continue;
            }
            int largest = findLargestContainingX(lower, upper, lowerDigits, upperDigits, x, i);

            //adjust the bounds if lower or upper is within the block of numbers containing x in given position i.
            int lowerAdjust = smallest % place;
            smallest -= lowerAdjust;
            int upperAdjust = place - 1 - (largest%place);
            largest += upperAdjust;

            int range = largest - smallest;

            //number of times the digit x occurs in position i within the given range
            int num = ((range / divisor) + 1) * place;

            //readjust if lower or upper is within the block of numbers containing x in given position i.
            num -= (lowerAdjust + upperAdjust);

            count += num;


            place *= 10;
            divisor *= 10;
        }

        return count;
    }

    private static void testSmallestContainingX1() {
        int lower = 22;
        int upper = 26;
        int x = 2;
        int position = 0;
        int expected = 22;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX2() {
        int lower = 22;
        int upper = 26;
        int x = 3;
        int position = 0;
        int expected = 23;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX3() {
        int lower = 22;
        int upper = 36;
        int x = 1;
        int position = 0;
        int expected = 31;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX4() {
        int lower = 22;
        int upper = 26;
        int x = 7;
        int position = 0;
        int expected = -1;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX5() {
        int lower = 23;
        int upper = 47;
        int x = 2;
        int position = 1;
        int expected = 23;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX6() {
        int lower = 23;
        int upper = 47;
        int x = 3;
        int position = 1;
        int expected = 30;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX7() {
        int lower = 23;
        int upper = 47;
        int x = 6;
        int position = 1;
        int expected = -1;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX8() {
        int lower = 42;
        int upper = 295;
        int x = 6;
        int position = 1;
        int expected = 60;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX9() {
        int lower = 42;
        int upper = 295;
        int x = 1;
        int position = 2;
        int expected = 100;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX10() {
        int lower = 42;
        int upper = 295;
        int x = 0;
        int position = 2;
        int expected = -1;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX11() {
        int lower = 42;
        int upper = 295;
        int x = 0;
        int position = 1;
        int expected = 100;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX12() {
        int lower = 42;
        int upper = 295;
        int x = 0;
        int position = 0;
        int expected = 50;
        int actual = findSmallestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testSmallestContainingX() {
        testSmallestContainingX1();
        testSmallestContainingX2();
        testSmallestContainingX3();
        testSmallestContainingX4();
        testSmallestContainingX5();
        testSmallestContainingX6();
        testSmallestContainingX7();
        testSmallestContainingX8();
        testSmallestContainingX9();
        testSmallestContainingX10();
        testSmallestContainingX11();
        testSmallestContainingX12();
    }

    private static void testLargestContainingX1() {
        int lower = 22;
        int upper = 26;
        int x = 6;
        int position = 0;
        int expected = 26;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX2() {
        int lower = 22;
        int upper = 26;
        int x = 3;
        int position = 0;
        int expected = 23;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX3() {
        int lower = 22;
        int upper = 36;
        int x = 8;
        int position = 0;
        int expected = 28;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX4() {
        int lower = 22;
        int upper = 26;
        int x = 7;
        int position = 0;
        int expected = -1;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX5() {
        int lower = 22;
        int upper = 64;
        int x = 3;
        int position = 0;
        int expected = 63;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX6() {
        int lower = 22;
        int upper = 64;
        int x = 3;
        int position = 1;
        int expected = 39;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX7() {
        int lower = 22;
        int upper = 64;
        int x = 1;
        int position = 1;
        int expected = -1;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX8() {
        int lower = 22;
        int upper = 64;
        int x = 8;
        int position = 1;
        int expected = -1;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX9() {
        int lower = 22;
        int upper = 564;
        int x = 1;
        int position = 1;
        int expected = 519;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX10() {
        int lower = 22;
        int upper = 564;
        int x = 8;
        int position = 1;
        int expected = 489;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX11() {
        int lower = 22;
        int upper = 564;
        int x = 6;
        int position = 1;
        int expected = 564;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX12() {
        int lower = 22;
        int upper = 564;
        int x = 2;
        int position = 2;
        int expected = 299;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX13() {
        int lower = 22;
        int upper = 564;
        int x = 6;
        int position = 2;
        int expected = -1;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX14() {
        int lower = 22;
        int upper = 564;
        int x = 0;
        int position = 2;
        int expected = -1;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX15() {
        int lower = 22;
        int upper = 564;
        int x = 0;
        int position = 1;
        int expected = 509;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX16() {
        int lower = 22;
        int upper = 564;
        int x = 0;
        int position = 0;
        int expected = 560;
        int actual = findLargestContainingX(lower, upper, toDigits(lower), toDigits(upper), x, position);

        assert (expected == actual);
    }

    private static void testLargestContainingX() {
        testLargestContainingX1();
        testLargestContainingX2();
        testLargestContainingX3();
        testLargestContainingX4();
        testLargestContainingX5();
        testLargestContainingX6();
        testLargestContainingX7();
        testLargestContainingX8();
        testLargestContainingX9();
        testLargestContainingX10();
        testLargestContainingX11();
        testLargestContainingX12();
        testLargestContainingX13();
        testLargestContainingX14();
        testLargestContainingX15();
        testLargestContainingX16();
    }

    private static void testSimple(int lower, int upper, int x, int expected) {
        int actual = howManyXSimple(lower, upper, x);
        assert (expected == actual);
    }

    private static void testFast(int lower, int upper, int x, int expected) {
        int actual = howManyXFast(lower, upper, x);
        assert (expected == actual);
    }

    private static void test1() {
        testSimple(100, 250, 3, 35);
    }

    private static void test2() {
        testSimple(10000, 12345, 2, 1120);
    }

    private static void test3() {
        testSimple(20, 21, 0, 0);
    }

    private static void test4() {
        testSimple(899, 1000, 9, 120);
    }

    private static void test5() {
        testSimple(1100, 1345, 1, 398);
    }

    private static void test6() {
        testFast(100, 250, 3, 35);
    }

    private static void test7() {
        testFast(10000, 12345, 2, 1120);
    }

    private static void test8() {
        testFast(20, 21, 0, 0);
    }

    private static void test9() {
        testFast(899, 1000, 9, 120);
    }

    private static void test10() {
        testFast(1100, 1345, 1, 398);
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
        test9();
        test10();
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
        testSmallestContainingX();
        testLargestContainingX();
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
            int count = howManyXFast(testCase.lower, testCase.upper, testCase.x);
            System.out.println(count);
        }
    }
}
