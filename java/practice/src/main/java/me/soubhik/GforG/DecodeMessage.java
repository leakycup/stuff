package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by soubhik on 29-11-2018.
 */
public class DecodeMessage {
    private static final Set<String> validTokens = new HashSet<>();
    static {
        for (int i = 1; i <= 26; i++) {
            validTokens.add(Integer.toString(i));
        }
    }

    private static int numDecodes(String input) {
        if (input.isEmpty()) {
            return 1;
        }

        int[] memo = new int[input.length()];

        String prefix = input.substring(0, 1);
        memo[0] = (validTokens.contains(prefix)) ? 1:0;

        if (input.length() > 1) {
            prefix = input.substring(1, 2);
            if (validTokens.contains(prefix)) {
                memo[1] = memo[0];
            }

            prefix = input.substring(0, 2);
            if (validTokens.contains(prefix)) {
                memo[1] += 1;
            }
        }

        for (int i = 2; i < input.length(); i++) {
            String suffix = input.substring(i, i+1);
            if (validTokens.contains(suffix)) {
                memo[i] = memo[i-1];
            }
            suffix = input.substring(i-1, i+1);
            if (validTokens.contains(suffix)) {
                memo[i] += memo[i-2];
            }
        }

        return memo[input.length() - 1];
    }

    private static void test1() {
        int actual = numDecodes("");
        int expected = 1;
        assert (expected == actual);
    }

    private static void test2() {
        int actual = numDecodes("6");
        int expected = 1;
        assert (actual == expected);
    }

    private static void test3() {
        int actual = numDecodes("0");
        int expected = 0;
        assert (actual == expected);
    }

    private static void test4() {
        int actual = numDecodes("14");
        int expected = 2;
        assert (actual == expected);
    }

    private static void test5() {
        int actual = numDecodes("10");
        int expected = 1;
        assert (actual == expected);
    }

    private static void test6() {
        int actual = numDecodes("28");
        int expected = 1;
        assert (actual == expected);
    }

    private static void test7() {
        int actual = numDecodes("30");
        int expected = 0;
        assert (actual == expected);
    }

    private static void test8() {
        int actual = numDecodes("123");
        int expected = 3;
        assert (actual == expected);
    }

    private static void test9() {
        int actual = numDecodes("103");
        int expected = 1;
        assert (actual == expected);
    }

    private static void test10() {
        int actual = numDecodes("120");
        int expected = 1;
        assert (actual == expected);
    }

    private static void test11() {
        int actual = numDecodes("140");
        int expected = 0;
        assert (actual == expected);
    }

    private static void test12() {
        int actual = numDecodes("876");
        int expected = 1;
        assert (actual == expected);
    }

    private static void test13() {
        int actual = numDecodes("136");
        int expected = 2;
        assert (actual == expected);
    }

    private static void test14() {
        int actual = numDecodes("321");
        int expected = 2;
        assert (actual == expected);
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
    }

    private static class TestCase {
        private final String input;

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
                int n = Integer.parseInt(line);
                line = reader.readLine();
                testCases[i] = new TestCase(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int numDecodedMessages = numDecodes(testCase.input);
            System.out.println(numDecodedMessages);
        }
    }
}
