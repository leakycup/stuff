package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by soubhik on 19-11-2018.
 * from https://practice.geeksforgeeks.org/problems/string-palindromic-ignoring-spaces/0
 */
public class PalindromeIgnoringSpace {
    public static boolean isPalindrome(String input) {
        int start = 0;
        int end = input.length() - 1;

        while (start < end) {
            char c1 = input.charAt(start);
            if (c1 == ' ') {
                start++;
                continue;
            }

            char c2 = input.charAt(end);
            if (c2 == ' ') {
                end--;
                continue;
            }

            if (c1 != c2) {
                return false;
            }
            start++;
            end--;
        }

        return true;
    }

    private static void test1() {
        String input = "";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test2() {
        String input = " ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test3() {
        String input = "  ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test4() {
        String input = "   ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test5() {
        String input = "a";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test6() {
        String input = "aa";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test7() {
        String input = "aaa";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test8() {
        String input = "aba";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test9() {
        String input = " a";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test10() {
        String input = "a ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test11() {
        String input = " a";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test12() {
        String input = " aa";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test13() {
        String input = "aa ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test14() {
        String input = "a a";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test15() {
        String input = "a a ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test16() {
        String input = "a  a ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test17() {
        String input = "a ba";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test18() {
        String input = "ab a";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test19() {
        String input = "abb a";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test20() {
        String input = " abb  a   ";
        boolean actual = isPalindrome(input);
        boolean expected = true;

        assert (expected == actual);
    }

    private static void test21() {
        String input = "ab";
        boolean actual = isPalindrome(input);
        boolean expected = false;

        assert (expected == actual);
    }

    private static void test22() {
        String input = "a b";
        boolean actual = isPalindrome(input);
        boolean expected = false;

        assert (expected == actual);
    }

    private static void test23() {
        String input = " ab";
        boolean actual = isPalindrome(input);
        boolean expected = false;

        assert (expected == actual);
    }

    private static void test24() {
        String input = "ab ";
        boolean actual = isPalindrome(input);
        boolean expected = false;

        assert (expected == actual);
    }

    private static void test25() {
        String input = "ac  b a   ";
        boolean actual = isPalindrome(input);
        boolean expected = false;

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
        test9();
        test10();
        test11();
        test12();
        test13();
        test14();
        test15();
        test16();
        test17();
        test18();
        test19();
        test20();
        test21();
        test22();
        test23();
        test24();
        test25();
    }

    private static class TestCase {
        String input;

        public TestCase(String input) {
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
                testCases[i] = new TestCase(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            boolean palindrome = isPalindrome(testCase.input);
            if (palindrome) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }
}
