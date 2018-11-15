package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by soubhik on 15-11-2018.
 */
public class RearrangeString {
    private static final int LETTERS = 26;
    private static final char FIRST_LETTER = 'A';
    private static final char LAST_LETTER = 'Z';
    private static final char FIRST_DIGIT = '0';
    private static final char LAST_DIGIT = '9';

    public static String rearrange(String input) {
        int[] lettersCount = new int[LETTERS]; //0 initialized by java
        int sum = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c >= FIRST_DIGIT) && (c <= LAST_DIGIT)) {
                sum += c - FIRST_DIGIT;
            } else if ((c >= FIRST_LETTER) && (c <= LAST_LETTER)) {
                lettersCount[c - FIRST_LETTER]++;
            } else  {
                throw new IllegalArgumentException("expecting uppercase letters and digits only");
            }
        }

        StringBuilder outputBuilder = new StringBuilder();
        for (int i = 0; i < lettersCount.length; i++) {
            char letter = (char)(FIRST_LETTER + i);
            for (int j = 0; j < lettersCount[i]; j++) {
                outputBuilder.append(letter);
            }
        }
        if (sum > 0) {
            outputBuilder.append(sum);
        }

        return outputBuilder.toString();
    }

    private static void test1() {
        String input = "CABBTXT";
        String expected = "ABBCTTX";
        String actual = rearrange(input);

        assert (expected.equals(actual));
    }

    private static void test2() {
        String input = "CA2BBT4XT";
        String expected = "ABBCTTX6";
        String actual = rearrange(input);

        assert (expected.equals(actual));
    }

    private static void test3() {
        String input = "283";
        String expected = "13";
        String actual = rearrange(input);

        assert (expected.equals(actual));
    }

    private static void test4() {
        String input = "8XCP99ZO0P3ADCB";
        String expected = "ABCCDOPPXZ29";
        String actual = rearrange(input);

        assert (expected.equals(actual));
    }

    private static void test5() {
        String input = "MJEEAKCOUQWXWSJXBWAGMSDNFQ18W4P1";
        String expected = "AABCDEEFGJJKMMNOPQQSSUWWWWXX14";
        String actual = rearrange(input);

        assert (expected.equals(actual));
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
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
            String output = rearrange(testCase.input);
            System.out.println(output);
        }
    }
}
