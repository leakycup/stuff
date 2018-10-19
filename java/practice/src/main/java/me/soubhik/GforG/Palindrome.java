package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sb8 on 10/19/18.
 * from https://practice.geeksforgeeks.org/problems/save-ironman/0
 */
public class Palindrome {
    private static class TestCase {
        private String s;

        public TestCase(String s) {
            this.s = s;
        }
    }

    private static boolean isAlphaNumeric(String s, int index) {
        int codePoint = Character.codePointAt(s, index);
        return (Character.isAlphabetic(codePoint) || Character.isDigit(codePoint));
    }

    private static boolean isPalindrome(String s) {
        if (s.isEmpty()) {
            return true;
        }

        int i = 0;
        int j = s.length() - 1;
        while (true) {
            while ((i < j) && !isAlphaNumeric(s, i)) {
                i++;
            }

            while ((i < j) && !isAlphaNumeric(s, j)) {
                j--;
            }

            if (i < j) {
                char a = s.charAt(i);
                a = Character.toLowerCase(a);
                char b = s.charAt(j);
                b = Character.toLowerCase(b);
                if (a != b) {
                    return false;
                }
                i++;
                j--;
            } else {
                break;
            }
        }

        return true;
    }

    private static void test1() {
        boolean actual = isPalindrome("");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test2() {
        boolean actual = isPalindrome("  ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test3() {
        boolean actual = isPalindrome(" !");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test4() {
        boolean actual = isPalindrome("a");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test5() {
        boolean actual = isPalindrome("a ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test6() {
        boolean actual = isPalindrome(" a");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test7() {
        boolean actual = isPalindrome(" a ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test8() {
        boolean actual = isPalindrome(" a  ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test9() {
        boolean actual = isPalindrome(" a   ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test10() {
        boolean actual = isPalindrome("ab");
        boolean expected = false;
        assert (expected == actual);
    }

    private static void test11() {
        boolean actual = isPalindrome("aba");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test12() {
        boolean actual = isPalindrome("ab#a");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test13() {
        boolean actual = isPalindrome("ab#a ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test14() {
        boolean actual = isPalindrome("ab#a ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test15() {
        boolean actual = isPalindrome("a b#a ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test16() {
        boolean actual = isPalindrome("!a b#a ");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test17() {
        boolean actual = isPalindrome("!a b#a b");
        boolean expected = false;
        assert (expected == actual);
    }

    private static void test18() {
        boolean actual = isPalindrome("!a b#b a");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test19() {
        boolean actual = isPalindrome("abba");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test20() {
        boolean actual = isPalindrome("1bb1");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test21() {
        boolean actual = isPalindrome("1bb3");
        boolean expected = false;
        assert (expected == actual);
    }

    private static void test22() {
        boolean actual = isPalindrome("I am :IronnorI Ma, i");
        boolean expected = true;
        assert (expected == actual);
    }

    private static void test23() {
        boolean actual = isPalindrome("Ab?/Ba");
        boolean expected = true;
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
            boolean answer = isPalindrome(testCase.s);
            if (answer) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }
}
